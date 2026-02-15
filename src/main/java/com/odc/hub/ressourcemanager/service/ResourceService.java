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
        Resource resource = resourceMapper.toEntity(request, createdBy);

        if (file != null && !file.isEmpty()) {
            String fileId = gridFsService.uploadFile(file);
            resource.setGridFsFileId(fileId);
            resource.setFilename(file.getOriginalFilename());
        }

        Resource saved = resourceRepository.save(resource);
        return resourceMapper.toResponse(saved);
    }

    public List<ResourceResponse> getResourcesByModule(
            String moduleId,
            boolean onlyValidated,
            String userEmail) {
        List<Resource> resources = onlyValidated
                ? resourceRepository.findByModuleIdAndValidatedTrue(moduleId)
                : resourceRepository.findByModuleId(moduleId);

        // Filter by assignedTo if user is a bootcamper
        return userRepository.findByEmail(userEmail)
                .map(user -> {
                    if (user.getRole() == Role.BOOTCAMPER) {
                        return resources.stream()
                                .filter(r -> r.getAssignedTo() == null ||
                                        r.getAssignedTo().isEmpty() ||
                                        r.getAssignedTo().contains(user.getId()))
                                .map(this::mapToResponseWithStats)
                                .collect(Collectors.toList());
                    } else {
                        return resources.stream()
                                .map(this::mapToResponseWithStats)
                                .collect(Collectors.toList());
                    }
                })
                .orElse(resources.stream()
                        .map(this::mapToResponseWithStats)
                        .collect(Collectors.toList()));
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

    public List<ResourceResponse> getAllResources(boolean onlyValidated) {
        List<Resource> resources = onlyValidated
                ? resourceRepository.findByValidatedTrue()
                : resourceRepository.findAll();

        return resources.stream()
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
