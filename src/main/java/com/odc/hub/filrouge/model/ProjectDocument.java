package com.odc.hub.filrouge.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "projects")
public class ProjectDocument {

    @Id
    private String id;

    private String name;
    private String description;

    private String createdBy;
    private ArrayList<String> members = new ArrayList<>();

    private Instant createdAt;

}
