package com.odc.hub.ressourcemanager.model;

import com.odc.hub.ressourcemanager.enums.ResourceType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "resources")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resource {

    @Id
    private String id;

    private String title;
    private String moduleId;

    private ResourceType type;

    private String gridFsFileId;
    private String link;

    private String createdBy;
    private boolean validated;

    @Builder.Default
    private Instant createdAt = Instant.now();
}
