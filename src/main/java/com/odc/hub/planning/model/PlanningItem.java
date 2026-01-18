package com.odc.hub.planning.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "planning_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanningItem {

    @Id
    private String id;

    private String title;
    private String description;

    private PlanningItemType type; // SESSION, EVENT, DEADLINE

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Field("userIds")
    private List<String> userIds;

    private List<String> tags;

    private String createdBy; // admin / formateur
}

