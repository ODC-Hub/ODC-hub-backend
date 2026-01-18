package com.odc.hub.planning.controller;

import com.odc.hub.planning.dto.PlanningItemRequest;
import com.odc.hub.planning.dto.PlanningItemResponse;
import com.odc.hub.planning.service.PlanningItemService;
import com.odc.hub.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planning")
@RequiredArgsConstructor
public class PlanningItemController {

    private final PlanningItemService planningService;

    // Only ADMIN or FORMATEUR can create
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR')")
    public PlanningItemResponse create(
            @RequestBody PlanningItemRequest request,
            @AuthenticationPrincipal User user
    ) {
        return planningService.create(request, user);
    }

    // EVERYONE can read
    @GetMapping
    public List<PlanningItemResponse> myPlanning(@AuthenticationPrincipal User user) {
        return planningService.getPlanningForUser(user);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR')")
    public PlanningItemResponse update(
            @PathVariable String id,
            @RequestBody PlanningItemRequest request,
            @AuthenticationPrincipal User user
    ) {
        return planningService.update(id, request, user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR')")
    public void delete(
            @PathVariable String id,
            @AuthenticationPrincipal User user
    ) {
        planningService.delete(id, user);
    }

}
