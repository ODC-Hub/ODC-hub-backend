package com.odc.hub.recommendations.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.bson.types.ObjectId;
import com.fasterxml.jackson.annotation.JsonIgnore;


@Document(collection = "resources-v2")
@Data
public class Resource {

    @Id
    @JsonIgnore
    private org.bson.types.ObjectId _id;

    private String id;

    private String title;
    private String description;
    private String type;
    private String category;

    private String url;
}
