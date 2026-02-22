package com.odc.hub.ressourcemanager.service;

import com.odc.hub.ressourcemanager.dto.ResourceCreateRequest;
import com.odc.hub.ressourcemanager.dto.ResourceResponse;
import com.odc.hub.ressourcemanager.enums.ResourceType;
import com.odc.hub.ressourcemanager.exception.NotFoundException;
import com.odc.hub.ressourcemanager.mapper.ResourceMapper;
import com.odc.hub.ressourcemanager.model.Resource;
import com.odc.hub.ressourcemanager.repository.ResourceRepository;
import com.odc.hub.user.model.Role;
import com.odc.hub.user.model.User;
import com.odc.hub.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final ResourceNotificationService resourceNotificationService;

    // CREATE
    public ResourceResponse createResource(
            ResourceCreateRequest request,
            MultipartFile file,
            User creator
    ) {
        Resource resource = resourceMapper.toEntity(request, creator.getId());

        if (file != null && !file.isEmpty()) {
            resource.setGridFsFileId(gridFsService.uploadFile(file));
            resource.setFilename(file.getOriginalFilename());
        }

        Resource saved = resourceRepository.save(resource);

        if (saved.getType() != ResourceType.HOMEWORK) {
            resourceNotificationService.onResourceCreated(saved, creator);
        }

        return resourceMapper.toResponse(saved);
    }

    // READ
    public List<ResourceResponse> getResourcesByModule(
            String moduleId,
            boolean onlyValidated,
            User user
    ) {
        List<Resource> resources = onlyValidated
                ? resourceRepository.findByModuleIdAndValidatedTrue(moduleId)
                : resourceRepository.findByModuleId(moduleId);

        return filterResourcesByUser(resources, user);
    }

    public List<ResourceResponse> getAllResources(boolean onlyValidated, User user) {
        List<Resource> resources = onlyValidated
                ? resourceRepository.findByValidatedTrue()
                : resourceRepository.findAll();

        return filterResourcesByUser(resources, user);
    }

    // UPDATE
    public void validateResource(String resourceId, User validator) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new NotFoundException("Resource not found"));

        if (resource.isValidated()) {
            return;
        }
        resource.setValidated(true);
        resourceRepository.save(resource);

        if (resource.getType() == ResourceType.HOMEWORK) {
            resourceNotificationService.onHomeworkAssigned(resource, validator);
        } else {
            resourceNotificationService.onResourceCreated(resource, validator);
            // OR: onResourceValidated(...) if you want a separate event
        }
    }

    // DELETE
    public void deleteResource(String id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Resource not found"));
        // delete any GridFS file
        if (resource.getGridFsFileId() != null) {
            try {
                gridFsService.deleteFile(resource.getGridFsFileId());
            } catch (Exception e) {
                log.warn("Failed to delete GridFS file {} for resource {}: {}", resource.getGridFsFileId(), id, e.getMessage());
            }
        }
        resourceRepository.delete(resource);
    }

    // INTERNAL FILTERING
    private List<ResourceResponse> filterResourcesByUser(
            List<Resource> resources,
            User user
    ) {
        if (user.getRole() == Role.BOOTCAMPER) {
            return resources.stream()
                    .filter(r -> {
                        // Homework → ONLY assigned users
                        if (r.getType() == ResourceType.HOMEWORK) {
                            return r.getAssignedTo() != null
                                    && r.getAssignedTo().contains(user.getId());
                        }

                        // Other resources → visible to all
                        return true;
                    })
                    .map(this::mapToResponseWithStats)
                    .collect(Collectors.toList());
        }

        // ADMIN / FORMATEUR
        return resources.stream()
                .map(this::mapToResponseWithStats)
                .collect(Collectors.toList());
    }

    private ResourceResponse mapToResponseWithStats(Resource resource) {
        ResourceResponse response = resourceMapper.toResponse(resource);

        if (resource.getType() == ResourceType.HOMEWORK) {
            response.setTotalSubmissions(
                    livrableRepository.countByResourceId(resource.getId()));
            response.setPendingSubmissions(
                    livrableRepository.countByResourceIdAndStatus(
                            resource.getId(),
                            com.odc.hub.ressourcemanager.enums.LivrableStatus.PENDING));
        }
        return response;
    }
}