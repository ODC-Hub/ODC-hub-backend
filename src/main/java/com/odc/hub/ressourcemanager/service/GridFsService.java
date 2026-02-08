package com.odc.hub.ressourcemanager.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@Service
@RequiredArgsConstructor
public class GridFsService {

    private final GridFsTemplate gridFsTemplate;

    // Upload file
    public String uploadFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {

            ObjectId fileId = gridFsTemplate.store(
                    inputStream,
                    file.getOriginalFilename(),
                    file.getContentType()
            );

            return fileId.toHexString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to GridFS", e);
        }
    }

    // Get file
    public GridFsResource getFile(String fileId) {
        Query query = Query.query(
                Criteria.where("_id").is(new ObjectId(fileId))
        );

        return gridFsTemplate.getResource(
                gridFsTemplate.findOne(query)
        );
    }

    // Delete file
    public void deleteFile(String fileId) {
        Query query = Query.query(
                Criteria.where("_id").is(new ObjectId(fileId))
        );

        gridFsTemplate.delete(query);
    }
}
