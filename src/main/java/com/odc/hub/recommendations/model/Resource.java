package com.odc.hub.recommendations.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.bson.types.ObjectId;
import com.fasterxml.jackson.annotation.JsonIgnore;


@Document(collection = "resources-v1")
@Data
public class Resource {

    @Id
    private String id;

    private String title;
    private String description;
    private String type;
    private String category;

    private String url;
}
