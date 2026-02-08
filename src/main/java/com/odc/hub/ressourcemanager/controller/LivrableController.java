package com.odc.hub.ressourcemanager.controller;

import com.odc.hub.ressourcemanager.dto.LivrableCreateRequest;
import com.odc.hub.ressourcemanager.dto.LivrableResponse;
import com.odc.hub.ressourcemanager.enums.LivrableStatus;
import com.odc.hub.ressourcemanager.service.LivrableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/livrables")
@RequiredArgsConstructor
public class LivrableController {

    private final LivrableService livrableService;

    @PreAuthorize("hasRole('BOOTCAMPER')")
    @PostMapping
    public ResponseEntity<LivrableResponse> submitLivrable(
            @RequestPart("data") LivrableCreateRequest request,
            @RequestPart("file") MultipartFile file,
            Principal principal
    ) {
        return ResponseEntity.ok(
                livrableService.submitLivrable(
                        request,
                        file,
                        principal.getName()
                )
        );
    }

    @PatchMapping("/{id}/review")
    public ResponseEntity<LivrableResponse> reviewLivrable(
            @PathVariable String id,
            @RequestParam LivrableStatus status,
            @RequestParam(required = false) String comment,
            Principal principal
    ) {
        return ResponseEntity.ok(
                livrableService.reviewLivrable(
                        id,
                        status,
                        comment,
                        principal.getName()
                )
        );
    }
}
