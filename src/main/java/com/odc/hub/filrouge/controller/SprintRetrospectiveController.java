package com.odc.hub.filrouge.controller;

import com.odc.hub.filrouge.dto.SprintRetrospectiveResponse;
import com.odc.hub.filrouge.service.SprintRetrospectiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/filrouge/retrospective")
@RequiredArgsConstructor
public class SprintRetrospectiveController {

    private final SprintRetrospectiveService retrospectiveService;

    @GetMapping("/sprint/{sprintId}")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR')")
    public SprintRetrospectiveResponse getSprintReport(@PathVariable String sprintId) {
        return retrospectiveService.generate(sprintId);
    }
}
