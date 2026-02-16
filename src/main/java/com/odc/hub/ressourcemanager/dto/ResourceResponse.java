package com.odc.hub.ressourcemanager.dto;

import com.odc.hub.ressourcemanager.enums.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceResponse {

    private String id;
    private String title;
    private String moduleId;
    private String description;
    private ResourceType type;

    private boolean hasFile;
    private String gridFsFileId;
    private String filename;
    private String link;

    private java.util.List<String> assignedTo;

    private long totalSubmissions;
    private long pendingSubmissions;

    private boolean validated;
    private Instant createdAt;

    // getters & setters
}
