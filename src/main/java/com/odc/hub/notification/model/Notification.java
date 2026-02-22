package com.odc.hub.notification.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "notifications")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    private String id;

    private String recipientId;
    private String actorId;

    private NotificationType type;

    private String title;
    private String message;

    private String entityType;
    private String entityId;

    private boolean read;

    private Instant createdAt;
}