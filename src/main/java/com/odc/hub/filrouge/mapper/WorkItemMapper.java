package com.odc.hub.filrouge.mapper;

import com.odc.hub.filrouge.dto.WorkItemResponse;
import com.odc.hub.filrouge.model.WorkItemDocument;

public class WorkItemMapper {

    public static WorkItemResponse toResponse(WorkItemDocument doc) {
        return new WorkItemResponse(
                doc.getId(),
                doc.getProjectId(),
                doc.getSprintId(),
                doc.getTitle(),
                doc.getDescription(),
                doc.getType(),
                doc.getStatus(),
                doc.getEffort(),
                doc.getDeadline(),
                doc.getAssignedUserIds(),
                doc.getCarryCount()
        );
    }
}
