package com.odc.hub.recommendations.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "modules-v1")
@Data
public class Module {

    @Id
    private String id;

    private String title;
    private String description;
    private String category;
}
