package com.odc.hub.user.controller;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.odc.hub.user.service.AvatarService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/users/avatar")
public class AvatarController {
    private final AvatarService avatarService;
    private final GridFsTemplate gridFsTemplate;

    public AvatarController(AvatarService avatarService, GridFsTemplate gridFsTemplate) {
        this.avatarService = avatarService;
        this.gridFsTemplate = gridFsTemplate;
    }

    @PostMapping("/me")
    public Map<String, String> uploadAvatar(
            @RequestParam("file") MultipartFile file) {
        return avatarService.uploadAvatar(file);
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<Resource> getAvatarById(@PathVariable ObjectId fileId) {

        GridFSFile file = gridFsTemplate.findOne(
                Query.query(Criteria.where("_id").is(fileId)));

        if (file == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Resource resource = gridFsTemplate.getResource(file);

        String contentType = file.getMetadata() != null
                ? file.getMetadata().getString("contentType")
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS))
                .body(resource);
    }
}
