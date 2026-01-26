package com.odc.hub.filrouge.controller;

import com.odc.hub.filrouge.dto.UserKpiResponse;
import com.odc.hub.filrouge.service.UserKpiService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/filrouge/kpis/users")
@RequiredArgsConstructor
public class UserKpiController {

    private final UserKpiService userKpiService;

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR')")
    public UserKpiResponse getUserKpis(@PathVariable String userId) {
        return userKpiService.computeUserKpis(userId);
    }
}
