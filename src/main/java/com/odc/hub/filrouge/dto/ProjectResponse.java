package com.odc.hub.filrouge.dto;

import java.time.Instant;
import java.util.List;

public record ProjectResponse(
        String id,
        String name,
        String description,
        String createdBy,
        List<String> members,
        Instant createdAt
) {}
