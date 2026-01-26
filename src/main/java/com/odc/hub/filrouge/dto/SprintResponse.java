package com.odc.hub.filrouge.dto;

import com.odc.hub.filrouge.enums.SprintStatus;

import java.time.Instant;

public record SprintResponse(
        String id,
        String projectId,
        String name,
        Instant startDate,
        Instant endDate,
        SprintStatus status,
        Integer plannedEffort
) {}
