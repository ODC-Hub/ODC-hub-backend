package com.odc.hub.filrouge.dto;

import java.time.Instant;

public record CreateSprintRequest(
        String name,
        Instant startDate,
        Instant endDate
) {}
