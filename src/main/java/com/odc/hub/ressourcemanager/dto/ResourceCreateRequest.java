package com.odc.hub.ressourcemanager.dto;

import com.odc.hub.ressourcemanager.enums.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceCreateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String moduleId;

    @NotNull
    private ResourceType type;

    private String link;
}
