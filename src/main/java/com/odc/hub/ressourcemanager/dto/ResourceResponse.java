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
    private ResourceType type;

    private boolean hasFile;
    private String link;

    private boolean validated;
    private Instant createdAt;

    // getters & setters
}
