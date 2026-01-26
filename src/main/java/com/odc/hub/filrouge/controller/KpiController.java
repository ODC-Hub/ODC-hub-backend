package com.odc.hub.filrouge.controller;

import com.odc.hub.filrouge.dto.ProjectKpiResponse;
import com.odc.hub.filrouge.service.KpiService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/filrouge/kpis")
@RequiredArgsConstructor
public class KpiController {

    private final KpiService kpiService;

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR','BOOTCAMPER')")
    public ProjectKpiResponse getProjectKpis(@PathVariable String projectId) {
        return kpiService.computeProjectKpis(projectId);
    }
}
