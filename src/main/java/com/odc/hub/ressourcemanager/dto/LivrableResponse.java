package com.odc.hub.ressourcemanager.dto;

import com.odc.hub.ressourcemanager.enums.LivrableStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LivrableResponse {

    private String id;
    private String resourceId;
    private String bootcamperId;

    private LivrableStatus status;
    private String reviewerComment;

    private String studentComment;
    private String bootcamperName;

    private String fileId;
    private String filename;

    private Instant submittedAt;

    // getters & setters
}
