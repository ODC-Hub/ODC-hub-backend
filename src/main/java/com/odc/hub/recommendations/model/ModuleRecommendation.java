package com.odc.hub.recommendations.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "module_recommendations")
@Data
public class ModuleRecommendation {

    @Id
    private String id;

    private String moduleId;

    private List<RecommendedResource> recommendedResources;
}