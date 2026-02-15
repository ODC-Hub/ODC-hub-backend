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
            String userEmail) {
        log.info("Fetching resources for module: {} and user: {}", moduleId, userEmail);
        List<Resource> resources = onlyValidated
                ? resourceRepository.findByModuleIdAndValidatedTrue(moduleId)
                : resourceRepository.findByModuleId(moduleId);

        return filterResourcesByUser(resources, userEmail);
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

    public List<ResourceResponse> getAllResources(boolean onlyValidated, String userEmail) {
        log.info("Fetching all resources for user: {}", userEmail);
        List<Resource> resources = onlyValidated
                ? resourceRepository.findByValidatedTrue()
                : resourceRepository.findAll();

        return filterResourcesByUser(resources, userEmail);
    }

    private List<ResourceResponse> filterResourcesByUser(List<Resource> resources, String userEmail) {
        return userRepository.findByEmail(userEmail)
                .map(user -> {
                    log.info("Filtering for User: {} with role: {}", user.getEmail(), user.getRole());
                    if (user.getRole() == Role.BOOTCAMPER) {
                        List<ResourceResponse> filtered = resources.stream()
                                .filter(r -> {
                                    // Logic: Visible if assignedTo is null/empty OR explicitly assigned to user
                                    boolean assignedToAll = r.getAssignedTo() == null || r.getAssignedTo().isEmpty();
                                    boolean explicitlyAssigned = r.getAssignedTo() != null
                                            && r.getAssignedTo().contains(user.getId());

                                    boolean isVisible = assignedToAll || explicitlyAssigned;

                                    if (!isVisible) {
                                        log.debug("Hiding resource {} (assignedTo: {}) from user {}",
                                                r.getTitle(), r.getAssignedTo(), user.getId());
                                    }
                                    return isVisible;
                                })
                                .map(this::mapToResponseWithStats)
                                .collect(Collectors.toList());
                        log.info("Returning {} resources after filtering (original: {})", filtered.size(),
                                resources.size());
                        return filtered;
                    } else {
                        log.info("User is not a bootcamper, returning all {} resources", resources.size());
                        return resources.stream()
                                .map(this::mapToResponseWithStats)
                                .collect(Collectors.toList());
                    }
                })
                .orElseGet(() -> {
                    log.warn("User not found for email: {}, returning all resources", userEmail);
                    return resources.stream()
                            .map(this::mapToResponseWithStats)
                            .collect(Collectors.toList());
                });
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
