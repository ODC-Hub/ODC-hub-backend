package com.odc.hub.planning.dto;

import com.odc.hub.planning.model.PlanningItemType;
import java.time.LocalDateTime;
import java.util.List;

public record PlanningItemResponse(
        String id,
        String title,
        String description,
        PlanningItemType type,
        LocalDateTime startDate,
        LocalDateTime endDate,
        List<String> userIds,
        List<String> tags,
        String createdBy
) {}
