package com.odc.hub.filrouge.dto;

import com.odc.hub.filrouge.enums.WorkItemType;

import java.time.Instant;
import java.util.List;

public record CreateWorkItemRequest(
        String title,
        String description,
        WorkItemType type,
        Integer effort,
        Instant deadline,
        List<String> assignedUserIds
) {}
