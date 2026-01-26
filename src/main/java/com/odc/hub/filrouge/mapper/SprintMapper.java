package com.odc.hub.filrouge.mapper;

import com.odc.hub.filrouge.dto.SprintResponse;
import com.odc.hub.filrouge.model.SprintDocument;

public class SprintMapper {

    public static SprintResponse toResponse(SprintDocument doc) {
        return new SprintResponse(
                doc.getId(),
                doc.getProjectId(),
                doc.getName(),
                doc.getStartDate(),
                doc.getEndDate(),
                doc.getStatus(),
                doc.getPlannedEffort()
        );
    }
}
