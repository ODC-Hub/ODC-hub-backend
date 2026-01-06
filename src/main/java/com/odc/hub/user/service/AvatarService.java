package com.odc.hub.user.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.odc.hub.user.model.User;
import com.odc.hub.user.repository.UserRepository;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AvatarService {

    private final GridFsTemplate gridFsTemplate;
    private final UserRepository userRepository;

    public Map<String, String> uploadAvatar(MultipartFile file) {

        User user = getCurrentUser();

        // 1️⃣ Validation
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty file");
        }

        String contentType = file.getContentType();
        List<String> allowedTypes = List.of(
                MediaType.IMAGE_JPEG_VALUE,
                MediaType.IMAGE_PNG_VALUE,
                "image/webp"
        );
        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only JPG, PNG, WEBP allowed");
        }

        if (file.getSize() > 2 * 1024 * 1024) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image too large (max 2MB)");
        }

        // 2️⃣ Delete old avatar
        if (user.getAvatarFileId() != null) {
            gridFsTemplate.delete(
                    Query.query(Criteria.where("_id").is(user.getAvatarFileId()))
            );
        }

        // 3️⃣ Store with metadata
        DBObject metadata = new BasicDBObject();
        metadata.put("contentType", contentType);
        metadata.put("userId", user.getId());

        ObjectId fileId;
        try {
            fileId = gridFsTemplate.store(
                    file.getInputStream(),
                    file.getOriginalFilename(),
                    contentType,
                    metadata
            );
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Upload failed");
        }

        user.setAvatarFileId(fileId);
        userRepository.save(user);

        return Map.of("avatarUrl", "/api/users/me/avatar");
    }

    public ResponseEntity<Resource> getAvatar() {

        User user = getCurrentUser();

        if (user.getAvatarFileId() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        GridFSFile gridFile = gridFsTemplate.findOne(
                Query.query(Criteria.where("_id").is(user.getAvatarFileId()))
        );

        if (gridFile == null) {
            user.setAvatarFileId(null);
            userRepository.save(user);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Resource resource = gridFsTemplate.getResource(gridFile);

        String contentType = gridFile.getMetadata() != null
                ? gridFile.getMetadata().getString("contentType")
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS))
                .body(resource);
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (!(principal instanceof User user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return user;
    }

}

