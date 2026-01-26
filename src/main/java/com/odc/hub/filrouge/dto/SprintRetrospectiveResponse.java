package com.odc.hub.filrouge.dto;

public record SprintRetrospectiveResponse(
        String sprintId,
        int plannedEffort,
        int completedEffort,
        int carryoverTasks,
        int overdueTasks,
        double reliabilityScore
) {}
