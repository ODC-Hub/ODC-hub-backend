package com.odc.hub.filrouge.dto;

public record UserKpiResponse(
        String userId,
        int assignedEffort,
        int completedEffort,
        int overdueTasks,
        double deliveryScore
) {}
