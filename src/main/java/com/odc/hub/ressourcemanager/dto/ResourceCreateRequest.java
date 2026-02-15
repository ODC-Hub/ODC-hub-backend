package com.odc.hub.ressourcemanager.dto;

import com.odc.hub.ressourcemanager.enums.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResourceCreateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String moduleId;

    @NotBlank
    private String description;

    @NotNull
    private ResourceType type;

    private String link;

    private java.util.List<String> assignedTo;
}
