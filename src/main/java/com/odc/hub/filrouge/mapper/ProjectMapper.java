package com.odc.hub.filrouge.mapper;

import com.odc.hub.filrouge.dto.ProjectResponse;
import com.odc.hub.filrouge.model.ProjectDocument;

public class ProjectMapper {

    public static ProjectResponse toResponse(ProjectDocument doc) {
        return new ProjectResponse(
                doc.getId(),
                doc.getName(),
                doc.getDescription(),
                doc.getCreatedBy(),
                doc.getMembers(),
                doc.getCreatedAt()
        );
    }
}
