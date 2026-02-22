package com.odc.hub.ressourcemanager.service;

import com.odc.hub.ressourcemanager.dto.ResourceCreateRequest;
import com.odc.hub.ressourcemanager.dto.ResourceResponse;
import com.odc.hub.ressourcemanager.exception.NotFoundException;
import com.odc.hub.ressourcemanager.mapper.ResourceMapper;
import com.odc.hub.ressourcemanager.model.Resource;
import com.odc.hub.ressourcemanager.repository.ResourceRepository;
import com.odc.hub.user.model.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final GridFsService gridFsService;
    private final com.odc.hub.ressourcemanager.repository.LivrableRepository livrableRepository;
    private final com.odc.hub.user.repository.UserRepository userRepository;

    public ResourceResponse createResource(
            ResourceCreateRequest request,
            MultipartFile file,
            String createdBy) {
        log.info("Creating resource with request: {}", request);
        Resource resource = resourceMapper.toEntity(request, createdBy);

        if (file != null && !file.isEmpty()) {
            String fileId = gridFsService.uploadFile(file);
            resource.setGridFsFileId(fileId);
            resource.setFilename(file.getOriginalFilename());
        }

        Resource saved = resourceRepository.save(resource);
        log.info("Resource saved with ID: {}, assignedTo: {}", saved.getId(), saved.getAssignedTo());
        return resourceMapper.toResponse(saved);
    }

    public List<ResourceResponse> getResourcesByModule(
            String moduleId,
            boolean onlyValidated,
            String userIdentifier) {
        log.info("Fetching resources for module: {} and userIdentifier: {}", moduleId, userIdentifier);
        List<Resource> resources = onlyValidated
                ? resourceRepository.findByModuleIdAndValidatedTrue(moduleId)
                : resourceRepository.findByModuleId(moduleId);

        return filterResourcesByUser(resources, userIdentifier);
    }

    public void validateResource(String resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new NotFoundException("Resource not found"));

        resource.setValidated(true);
        resourceRepository.save(resource);
    }

    public void deleteResource(String id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Resource not found"));
        resourceRepository.delete(resource);
    }

    public List<ResourceResponse> getAllResources(boolean onlyValidated, String userIdentifier) {
        log.info("Fetching all resources for userIdentifier: {}", userIdentifier);
        List<Resource> resources = onlyValidated
                ? resourceRepository.findByValidatedTrue()
                : resourceRepository.findAll();

        return filterResourcesByUser(resources, userIdentifier);
    }


    private List<ResourceResponse> filterResourcesByUser(List<Resource> resources, String userIdentifier) {
        Optional<com.odc.hub.user.model.User> optUser = Optional.empty();

        if (userIdentifier != null && !userIdentifier.isBlank()) {
            optUser = userRepository.findByEmail(userIdentifier);
            if (optUser.isEmpty()) {
                // fallback: maybe controller passed the user id rather than email
                optUser = userRepository.findById(userIdentifier);
            }
        } else {
            log.debug("No userIdentifier provided (null or blank)");
        }

        if (optUser.isEmpty()) {
            log.warn("User not found for identifier '{}'. Returning only public resources.", userIdentifier);
            return resources.stream()
                    .filter(r -> r.getAssignedTo() == null || r.getAssignedTo().isEmpty())
                    .map(this::mapToResponseWithStats)
                    .collect(Collectors.toList());
        }

        com.odc.hub.user.model.User user = optUser.get();
        log.info("Filtering resources for user id='{}' email='{}' role='{}'",
                user.getId(), user.getEmail(), user.getRole());

        // Admins / Formateurs see everything
        if (user.getRole() != Role.BOOTCAMPER) {
            return resources.stream()
                    .map(this::mapToResponseWithStats)
                    .collect(Collectors.toList());
        }

        // Bootcamper: only public resources or those explicitly assigned to them
        final String currentUserId = user.getId();
        return resources.stream()
                .filter(r -> {
                    // public (no assignment) => visible
                    if (r.getAssignedTo() == null || r.getAssignedTo().isEmpty()) {
                        return true;
                    }
                    // explicitly assigned => visible
                    return r.getAssignedTo().contains(currentUserId);
                })
                .map(this::mapToResponseWithStats)
                .collect(Collectors.toList());
    }

    private ResourceResponse mapToResponseWithStats(Resource resource) {
        ResourceResponse response = resourceMapper.toResponse(resource);
        if (resource.getType() == com.odc.hub.ressourcemanager.enums.ResourceType.HOMEWORK) {
            response.setTotalSubmissions(livrableRepository.countByResourceId(resource.getId()));
            response.setPendingSubmissions(livrableRepository.countByResourceIdAndStatus(resource.getId(),
                    com.odc.hub.ressourcemanager.enums.LivrableStatus.PENDING));
        }
        return response;
    }
}