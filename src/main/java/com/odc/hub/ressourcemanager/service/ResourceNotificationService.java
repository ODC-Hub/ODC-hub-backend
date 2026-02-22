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

    // RESOURCE CREATED
    public void onResourceCreated(Resource resource, User actor) {

        if (resource.getType() == ResourceType.HOMEWORK) return;

        userRepository.findByRole(Role.BOOTCAMPER).forEach(bootcamper ->
                notifyUser(
                        bootcamper,
                        actor,
                        NotificationType.RESOURCE_CREATED,
                        "New resource available",
                        "\"" + resource.getTitle() + "\" has been added",
                        resource.getId(),
                        EmailType.RESOURCE
                )
        );
    }

    // HOMEWORK ASSIGNED
    public void onHomeworkAssigned(Resource resource, User actor) {

        if (resource.getType() != ResourceType.HOMEWORK) return;
        if (resource.getAssignedTo() == null || resource.getAssignedTo().isEmpty()) return;

        resource.getAssignedTo().stream()
                .distinct()
                .forEach(userId ->
                        userRepository.findById(userId).ifPresent(bootcamper ->
                                notifyUser(
                                        bootcamper,
                                        actor,
                                        NotificationType.HOMEWORK_ASSIGNED,
                                        "New homework assigned",
                                        "\"" + resource.getTitle() + "\" has been assigned to you",
                                        resource.getId(),
                                        EmailType.LIVRABLE
                                )
                        )
                );
    }

    // HOMEWORK SUBMITTED
    public void onHomeworkSubmitted(Resource resource, Livrable livrable, User bootcamper) {

        userRepository.findById(resource.getCreatedBy()).ifPresent(creator ->
                notifyUser(
                        creator,
                        bootcamper,
                        NotificationType.HOMEWORK_SUBMITTED,
                        "Homework submitted",
                        bootcamper.getFullName() + " submitted \"" + resource.getTitle() + "\"",
                        resource.getId(),
                        EmailType.LIVRABLE
                )
        );
    }

    // HOMEWORK REVIEWED
    public void onHomeworkReviewed(Resource resource, Livrable livrable, User reviewer) {

        if (resource.getType() != ResourceType.HOMEWORK) return;
        if (livrable.getBootcamperId() == null) return;

        userRepository.findById(livrable.getBootcamperId())
                .filter(u -> resource.getAssignedTo() != null
                        && resource.getAssignedTo().contains(u.getId()))
                .ifPresent(bootcamper ->
                        notifyUser(
                                bootcamper,
                                reviewer,
                                NotificationType.HOMEWORK_REVIEWED,
                                "Homework reviewed",
                                "\"" + resource.getTitle() + "\" has been reviewed",
                                resource.getId(),
                                EmailType.LIVRABLE
                        )
                );
    }

    // INTERNAL DISPATCH
    private void notifyUser(
            User recipient,
            User actor,
            NotificationType type,
            String title,
            String message,
            String entityId,
            EmailType emailType
    ) {
        if (recipient.getId().equals(actor.getId())) return;

        // WebSocket + DB
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
        switch (emailType) {
            case RESOURCE -> emailService.sendResourceNotificationEmail(
                    recipient.getEmail(),
                    title,
                    message,
                    "http://localhost:5173/resources?resourceId=" + entityId            );
            case LIVRABLE -> emailService.sendLivrableNotificationEmail(
                    recipient.getEmail(),
                    title,
                    message,
                    "http://localhost:5173/resources?resourceId=" + entityId            );
        }
    }

    private enum EmailType {
        RESOURCE,
        LIVRABLE
    }
}