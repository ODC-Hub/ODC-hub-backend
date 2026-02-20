package com.odc.hub.recommendations.model;

import lombok.Data;

@Data
public class RecommendedResource {

    private String resourceId;
    private Double score;
}
