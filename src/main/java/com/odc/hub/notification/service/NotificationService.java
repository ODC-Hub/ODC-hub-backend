package com.odc.hub.notification.service;

import com.odc.hub.notification.model.Notification;
import com.odc.hub.notification.model.NotificationType;
import com.odc.hub.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    public Notification notify(
            String recipientId,
            String actorId,
            NotificationType type,
            String title,
            String message,
            String entityType,
            String entityId
    ) {
        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .actorId(actorId)
                .type(type)
                .title(title)
                .message(message)
                .entityType(entityType)
                .entityId(entityId)
                .read(false)
                .createdAt(Instant.now())
                .build();

        Notification saved = repository.save(notification);

        // 🔴 REAL-TIME PUSH
        if (!recipientId.equals(actorId)) {
            messagingTemplate.convertAndSendToUser(
                    recipientId,
                    "/queue/notifications",
                    saved
            );
        }

        return saved;    }
}