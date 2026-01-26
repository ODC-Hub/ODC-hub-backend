package com.odc.hub.filrouge.dto;

import java.util.List;

public record ProjectKpiResponse(
        String projectId,
        double globalProgress,
        List<SprintKpiResponse> sprintKpis
) {}
