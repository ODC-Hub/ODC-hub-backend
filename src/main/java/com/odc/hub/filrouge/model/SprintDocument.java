package com.odc.hub.filrouge.model;

import com.odc.hub.filrouge.enums.SprintStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "sprints")
public class SprintDocument {

    @Id
    private String id;

    private String projectId;

    private String name;

    private Instant startDate;
    private Instant endDate;

    private SprintStatus status;

    private Integer plannedEffort;

    public SprintDocument(Object o, String project1, String s, Instant now, Instant plus, SprintStatus sprintStatus, int i) {
    }
}
