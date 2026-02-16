package com.odc.hub.ressourcemanager.controller;

import com.odc.hub.ressourcemanager.dto.ResourceCreateRequest;
import com.odc.hub.ressourcemanager.dto.ResourceResponse;
import com.odc.hub.ressourcemanager.service.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {

        private final ResourceService resourceService;

        @PreAuthorize("hasAnyRole('FORMATEUR','ADMIN')")
        @PostMapping
        public ResponseEntity<ResourceResponse> createResource(
                        @Valid @RequestPart("data") ResourceCreateRequest request,
                        @RequestPart(value = "file", required = false) MultipartFile file,
                        Principal principal) {
                return ResponseEntity.ok(
                                resourceService.createResource(
                                                request,
                                                file,
                                                principal.getName()));
        }

        @GetMapping("/module/{moduleId}")
        public ResponseEntity<List<ResourceResponse>> getResourcesByModule(
                        @PathVariable String moduleId,
                        @RequestParam(defaultValue = "true") boolean validatedOnly,
                        Principal principal) {
                return ResponseEntity.ok(
                                resourceService.getResourcesByModule(moduleId, validatedOnly, principal.getName()));
        }

        @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR')")
        @PatchMapping("/{id}/validate")
        public ResponseEntity<Void> validateResource(@PathVariable String id) {
                resourceService.validateResource(id);
                return ResponseEntity.ok().build();
        }

        @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR')")
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteResource(@PathVariable String id) {
                resourceService.deleteResource(id);
                return ResponseEntity.ok().build();
        }

        @GetMapping
        public ResponseEntity<List<ResourceResponse>> getAllResources(
                        @RequestParam(defaultValue = "false") boolean validatedOnly,
                        Principal principal) {
                return ResponseEntity.ok(
                                resourceService.getAllResources(validatedOnly, principal.getName()));
        }
}
