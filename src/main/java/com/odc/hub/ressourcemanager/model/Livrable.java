package com.odc.hub.ressourcemanager.model;

import com.odc.hub.ressourcemanager.enums.LivrableStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "livrables")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Livrable {

    @Id
    private String id;

    private String resourceId;
    private String bootcamperId;

    private String gridFsFileId;

    @Builder.Default
    private LivrableStatus status = LivrableStatus.PENDING;

    private String reviewerComment;
    private String reviewedBy;

    @Builder.Default
    private Instant submittedAt = Instant.now();
}
