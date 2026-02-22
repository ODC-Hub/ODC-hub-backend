package com.odc.hub.notification.controller;

import com.odc.hub.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository repository;

    @GetMapping
    public Object getMyNotifications(Authentication auth) {
        String userId = auth.getName();
        return repository.findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    @GetMapping("/unread-count")
    public long unreadCount(Authentication auth) {
        return repository.countByRecipientIdAndReadFalse(auth.getName());
    }

    @PatchMapping("/{id}/read")
    public void markAsRead(@PathVariable String id) {
        repository.findById(id).ifPresent(n -> {
            n.setRead(true);
            repository.save(n);
        });
    }

}