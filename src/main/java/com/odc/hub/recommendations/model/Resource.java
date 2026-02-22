package com.odc.hub.recommendations.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "resources-v2")
@Data
public class Resource {

    @Id
    @JsonIgnore
    private ObjectId _id;

    private String id;

    private String title;
    private String description;
    private String type;
    private String url;
}