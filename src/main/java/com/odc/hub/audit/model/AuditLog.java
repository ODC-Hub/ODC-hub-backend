package com.odc.hub.audit.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Document(collection = "audit_logs")
public class AuditLog {

    @Id
    private String id;

    private String actorId;      // who did it
    private String actorEmail;

    private String targetUserId; // on whom
    private String action;       // APPROVE_USER, CHANGE_ROLE, DISABLE_USER...

    private String oldValue;
    private String newValue;

    private Instant createdAt;
}
