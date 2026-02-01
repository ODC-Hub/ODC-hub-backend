package com.odc.hub.filrouge.controller;

import com.odc.hub.filrouge.dto.CreateSprintRequest;
import com.odc.hub.filrouge.dto.SprintResponse;
import com.odc.hub.filrouge.mapper.SprintMapper;
import com.odc.hub.filrouge.model.SprintDocument;
import com.odc.hub.filrouge.service.SprintService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/filrouge/sprints")
@RequiredArgsConstructor
public class SprintController {

    private final SprintService sprintService;

    @PostMapping("/project/{projectId}")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR','BOOTCAMPER')")
    public SprintResponse createSprint(
            @PathVariable String projectId,
            @RequestBody CreateSprintRequest request) {
        SprintDocument sprint = sprintService.createSprint(projectId, request);

        return SprintMapper.toResponse(sprint);
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR','BOOTCAMPER')")
    public java.util.List<SprintResponse> getSprintsByProject(@PathVariable String projectId) {
        return sprintService.getSprintsByProject(projectId).stream()
                .map(SprintMapper::toResponse)
                .toList();
    }

    @PostMapping("/{sprintId}/start")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR','BOOTCAMPER')")
    public SprintResponse startSprint(@PathVariable String sprintId) {
        return SprintMapper.toResponse(
                sprintService.startSprint(sprintId));
    }

    @PostMapping("/{sprintId}/close")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR','BOOTCAMPER')")
    public void closeSprint(
            @PathVariable String sprintId,
            @RequestParam String nextSprintId) {
        sprintService.closeSprint(sprintId, nextSprintId);
    }
}
