package com.odc.hub.ressourcemanager.service;

import com.odc.hub.ressourcemanager.dto.ResourceCreateRequest;
import com.odc.hub.ressourcemanager.dto.ResourceResponse;
import com.odc.hub.ressourcemanager.exception.NotFoundException;
import com.odc.hub.ressourcemanager.mapper.ResourceMapper;
import com.odc.hub.ressourcemanager.model.Resource;
import com.odc.hub.ressourcemanager.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final GridFsService gridFsService;

    public ResourceResponse createResource(
            ResourceCreateRequest request,
            MultipartFile file,
            String createdBy
    ) {
        Resource resource = resourceMapper.toEntity(request, createdBy);

        if (file != null && !file.isEmpty()) {
            String fileId = gridFsService.uploadFile(file);
            resource.setGridFsFileId(fileId);
        }

        Resource saved = resourceRepository.save(resource);
        return resourceMapper.toResponse(saved);
    }

    public List<ResourceResponse> getResourcesByModule(
            String moduleId,
            boolean onlyValidated
    ) {
        List<Resource> resources = onlyValidated
                ? resourceRepository.findByModuleIdAndValidatedTrue(moduleId)
                : resourceRepository.findByModuleId(moduleId);

        return resources.stream()
                .map(resourceMapper::toResponse)
                .collect(Collectors.toList());
    }

    public void validateResource(String resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new NotFoundException("Resource not found"));

        resource.setValidated(true);
        resourceRepository.save(resource);
    }
}
