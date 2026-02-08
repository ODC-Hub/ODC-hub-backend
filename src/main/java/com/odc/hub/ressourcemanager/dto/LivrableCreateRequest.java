package com.odc.hub.ressourcemanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LivrableCreateRequest {

    @NotBlank
    private String resourceId;
    private String comment;

}
