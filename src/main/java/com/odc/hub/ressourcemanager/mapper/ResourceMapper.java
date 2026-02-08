package com.odc.hub.ressourcemanager.mapper;

import com.odc.hub.ressourcemanager.dto.ResourceCreateRequest;
import com.odc.hub.ressourcemanager.dto.ResourceResponse;
import com.odc.hub.ressourcemanager.model.Resource;
import org.springframework.stereotype.Component;

@Component
public class ResourceMapper {

    public Resource toEntity(ResourceCreateRequest request, String createdBy) {
        return Resource.builder()
                .title(request.getTitle())
                .moduleId(request.getModuleId())
                .type(request.getType())
                .link(request.getLink())
                .createdBy(createdBy)
                .validated(false)
                .build();
    }

    public ResourceResponse toResponse(Resource resource) {
        ResourceResponse response = new ResourceResponse();
        response.setId(resource.getId());
        response.setTitle(resource.getTitle());
        response.setModuleId(resource.getModuleId());
        response.setType(resource.getType());
        response.setLink(resource.getLink());
        response.setValidated(resource.isValidated());
        response.setCreatedAt(resource.getCreatedAt());
        response.setHasFile(resource.getGridFsFileId() != null);
        return response;
    }
}
