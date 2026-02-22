package com.odc.hub.filrouge.service;

import com.odc.hub.common.service.EmailService;
import com.odc.hub.notification.model.NotificationType;
import com.odc.hub.notification.service.NotificationService;
import com.odc.hub.filrouge.model.ProjectDocument;
import com.odc.hub.user.model.User;
import com.odc.hub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectNotificationService {

    private final NotificationService notificationService;
    private final EmailService emailService;
    private final UserRepository userRepository;

    public void onProjectCreated(ProjectDocument project, User creator) {

        if (project.getMembers() == null || project.getMembers().isEmpty()) {
            return;
        }

        project.getMembers().forEach(memberId -> {
            if (memberId.equals(creator.getId())) {
                return; // no self-notification
            }

            userRepository.findById(memberId).ifPresent(member ->
                    notifyUser(
                            member,
                            creator,
                            project
                    )
            );
        });
    }

    private void notifyUser(User recipient, User actor, ProjectDocument project) {

        notificationService.notify(
                recipient.getId(),
                actor.getId(),
                NotificationType.PROJECT_ASSIGNED,
                "New project assigned",
                "You have been added to project \"" + project.getName() + "\"",
                "PROJECT",
                project.getId()
        );

        emailService.sendProjectNotificationEmail(
                recipient.getEmail(),
                "New project assigned",
                "You have been added to project \"" + project.getName() + "\"",
                "http://localhost:5173/projects/" + project.getId()
        );
    }
}