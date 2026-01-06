package com.odc.hub.audit.dto;

import java.time.Instant;

public record AuditDTO(
        String actorEmail,
        String targetUserId,
        String action,
        String oldValue,
        String newValue,
        Instant createdAt
) {}
