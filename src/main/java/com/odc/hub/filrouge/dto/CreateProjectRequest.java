package com.odc.hub.filrouge.dto;

import java.util.List;

public record CreateProjectRequest(
        String name,
        String description,
        List<String> memberIds
) {}
