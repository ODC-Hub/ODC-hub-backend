package com.odc.hub.ressourcemanager.controller;

import com.odc.hub.ressourcemanager.service.GridFsService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final GridFsService gridFsService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{fileId}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable String fileId) throws Exception {

        GridFsResource resource = gridFsService.getFile(fileId);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + resource.getFilename() + "\""
                )
                .contentType(MediaType.parseMediaType(resource.getContentType()))
                .body(new InputStreamResource(resource.getInputStream()));
    }
}
