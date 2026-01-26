package com.odc.hub.filrouge.dto;

import com.odc.hub.filrouge.enums.WorkItemStatus;
import com.odc.hub.filrouge.enums.WorkItemType;

import java.time.Instant;
import java.util.List;

public record WorkItemResponse(
        String id,
        String projectId,
        String sprintId,
        String title,
        String description,
        WorkItemType type,
        WorkItemStatus status,
        Integer effort,
        Instant deadline,
        List<String> assignedUserIds,
        Integer carryCount
) {}
