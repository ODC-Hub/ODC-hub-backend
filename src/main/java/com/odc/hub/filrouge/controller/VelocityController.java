package com.odc.hub.filrouge.controller;

import com.odc.hub.filrouge.dto.SprintVelocityResponse;
import com.odc.hub.filrouge.service.VelocityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/filrouge/velocity")
@RequiredArgsConstructor
public class VelocityController {

    private final VelocityService velocityService;

    @GetMapping("/sprint/{sprintId}")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR')")
    public SprintVelocityResponse getSprintVelocity(@PathVariable String sprintId) {
        return velocityService.computeSprintVelocity(sprintId);
    }
}
