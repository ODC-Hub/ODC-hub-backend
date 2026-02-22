package com.odc.hub.ressourcemanager.mapper;

import com.odc.hub.ressourcemanager.dto.ResourceCreateRequest;
import com.odc.hub.ressourcemanager.dto.ResourceResponse;
import com.odc.hub.ressourcemanager.enums.ResourceType;
import com.odc.hub.ressourcemanager.model.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ResourceMapper {

    public Resource toEntity(ResourceCreateRequest request, String createdBy) {
        return Resource.builder()
                .title(request.getTitle())
                .moduleId(request.getModuleId())
                .description(request.getDescription())
                .type(request.getType())
                .link(request.getLink())
                .assignedTo(
                        request.getType() == ResourceType.HOMEWORK
                                ? Optional.ofNullable(request.getAssignedTo()).orElse(List.of())
                                : null
                )                .createdBy(createdBy)
                .validated(false)
                .build();
    }

    public ResourceResponse toResponse(Resource resource) {
        ResourceResponse response = new ResourceResponse();
        response.setId(resource.getId());
        response.setTitle(resource.getTitle());
        response.setModuleId(resource.getModuleId());
        response.setDescription(resource.getDescription());
        response.setType(resource.getType());
        response.setLink(resource.getLink());
        response.setAssignedTo(resource.getAssignedTo());
        response.setValidated(resource.isValidated());
        response.setCreatedAt(resource.getCreatedAt());
        response.setHasFile(resource.getGridFsFileId() != null);
        response.setGridFsFileId(resource.getGridFsFileId());
        response.setFilename(resource.getFilename());
        return response;
    }
}
