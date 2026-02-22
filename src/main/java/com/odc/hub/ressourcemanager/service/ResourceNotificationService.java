package com.odc.hub.ressourcemanager.service;

import com.odc.hub.common.service.EmailService;
import com.odc.hub.notification.model.NotificationType;
import com.odc.hub.notification.service.NotificationService;
import com.odc.hub.ressourcemanager.enums.ResourceType;
import com.odc.hub.ressourcemanager.model.Livrable;
import com.odc.hub.ressourcemanager.model.Resource;
import com.odc.hub.user.model.Role;
import com.odc.hub.user.model.User;
import com.odc.hub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceNotificationService {

    private final NotificationService notificationService;
    private final EmailService emailService;
    private final UserRepository userRepository;

    // Resource created → notify all bootcampers
    public void onResourceCreated(Resource resource, User actor) {

        // 🔒 Homework is handled separately
        if (resource.getType() == ResourceType.HOMEWORK) {
            return;
        }

        userRepository.findByRole(Role.BOOTCAMPER).forEach(bootcamper -> {
            notifyUser(
                    bootcamper,
                    actor,
                    NotificationType.RESOURCE_CREATED,
                    "New resource available",
                    "\"" + resource.getTitle() + "\" has been added",
                    resource.getId()
            );
        });
    }

    // Homework assigned → notify assigned bootcampers
    public void onHomeworkAssigned(Resource resource, User actor) {

        if (resource.getAssignedTo() == null || resource.getAssignedTo().isEmpty()) {
            return;
        }

        resource.getAssignedTo().forEach(userId ->
                userRepository.findById(userId).ifPresent(bootcamper ->
                        notifyUser(
                                bootcamper,
                                actor,
                                NotificationType.HOMEWORK_ASSIGNED,
                                "New homework assigned",
                                "\"" + resource.getTitle() + "\" has been assigned to you",
                                resource.getId()
                        )
                )
        );
    }

    // Homework submitted → notify creator (formateur)
    public void onHomeworkSubmitted(Resource resource, Livrable livrable, User bootcamper) {

        userRepository.findById(resource.getCreatedBy()).ifPresent(creator ->
                notifyUser(
                        creator,
                        bootcamper,
                        NotificationType.HOMEWORK_SUBMITTED,
                        "Homework submitted",
                        bootcamper.getFullName() + " submitted \"" + resource.getTitle() + "\"",
                        resource.getId()
                )
        );
    }

    // Homework reviewed → notify bootcamper
    public void onHomeworkReviewed(Resource resource, Livrable livrable, User reviewer) {

        userRepository.findById(livrable.getBootcamperId()).ifPresent(bootcamper ->
                notifyUser(
                        bootcamper,
                        reviewer,
                        NotificationType.HOMEWORK_REVIEWED,
                        "Homework reviewed",
                        "\"" + resource.getTitle() + "\" has been reviewed",
                        resource.getId()
                )
        );
    }

    // Internal helper (shared logic)
    private void notifyUser(
            User recipient,
            User actor,
            NotificationType type,
            String title,
            String message,
            String entityId
    ) {
        // no self-notifications
        if (recipient.getId().equals(actor.getId())) {
            return;
        }

        // In-app + WebSocket
        notificationService.notify(
                recipient.getId(),
                actor.getId(),
                type,
                title,
                message,
                "RESOURCE",
                entityId
        );

        // Email
        emailService.sendResourceNotificationEmail(
                recipient.getEmail(),
                title,
                message,
                "http://localhost:5173/resources/" + entityId
        );
    }
}