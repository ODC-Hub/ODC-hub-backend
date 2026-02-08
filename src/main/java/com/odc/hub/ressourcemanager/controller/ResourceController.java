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
            Principal principal
    ) {
        return ResponseEntity.ok(
                resourceService.createResource(
                        request,
                        file,
                        principal.getName()
                )
        );
    }

    @GetMapping("/module/{moduleId}")
    public ResponseEntity<List<ResourceResponse>> getResourcesByModule(
            @PathVariable String moduleId,
            @RequestParam(defaultValue = "true") boolean validatedOnly
    ) {
        return ResponseEntity.ok(
                resourceService.getResourcesByModule(moduleId, validatedOnly)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/validate")
    public ResponseEntity<Void> validateResource(@PathVariable String id) {
        resourceService.validateResource(id);
        return ResponseEntity.ok().build();
    }
}
