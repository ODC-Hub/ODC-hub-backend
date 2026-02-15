package com.odc.hub.ressourcemanager.controller;

import com.odc.hub.ressourcemanager.dto.LivrableCreateRequest;
import com.odc.hub.ressourcemanager.dto.LivrableResponse;
import com.odc.hub.ressourcemanager.enums.LivrableStatus;
import com.odc.hub.ressourcemanager.service.LivrableService;
import com.odc.hub.user.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/livrables")
@RequiredArgsConstructor
@Slf4j
public class LivrableController {

        private final LivrableService livrableService;

        @PreAuthorize("hasRole('BOOTCAMPER')")
        @PostMapping
        public ResponseEntity<LivrableResponse> submitLivrable(
                        @Valid @RequestPart("data") LivrableCreateRequest request,
                        @RequestPart("file") MultipartFile file,
                        @AuthenticationPrincipal User user) {
                log.info("Submission attempt for resource: {} by user: {}",
                                request.getResourceId(), user.getEmail());
                return ResponseEntity.ok(
                                livrableService.submitLivrable(
                                                request,
                                                file,
                                                user.getEmail()));
        }

        @PatchMapping("/{id}/review")
        public ResponseEntity<LivrableResponse> reviewLivrable(
                        @PathVariable String id,
                        @RequestParam LivrableStatus status,
                        @RequestParam(required = false) String comment,
                        @AuthenticationPrincipal User user) {
                return ResponseEntity.ok(
                                livrableService.reviewLivrable(
                                                id,
                                                status,
                                                comment,
                                                user.getId()));
        }

        @PreAuthorize("hasAnyRole('FORMATEUR', 'ADMIN')")
        @GetMapping("/resource/{resourceId}")
        public ResponseEntity<List<LivrableResponse>> getLivrablesByResource(@PathVariable String resourceId) {
                return ResponseEntity.ok(livrableService.getLivrablesByResource(resourceId));
        }

        @PreAuthorize("hasRole('BOOTCAMPER')")
        @GetMapping("/me")
        public ResponseEntity<List<LivrableResponse>> getMyLivrables(@AuthenticationPrincipal User user) {
                return ResponseEntity.ok(livrableService.getMyLivrables(user.getEmail()));
        }
}
