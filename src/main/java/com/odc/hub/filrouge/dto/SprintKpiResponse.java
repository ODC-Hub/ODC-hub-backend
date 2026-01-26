package com.odc.hub.filrouge.dto;

public record SprintKpiResponse(
        String sprintId,
        int plannedEffort,
        int completedEffort,
        double progressPercentage,
        int overdueItems,
        double riskScore
) {}
