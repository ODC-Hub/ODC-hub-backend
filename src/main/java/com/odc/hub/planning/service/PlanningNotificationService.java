package com.odc.hub.planning.service;

import com.odc.hub.notification.model.NotificationType;
import com.odc.hub.notification.service.NotificationService;
import com.odc.hub.planning.model.PlanningItem;
import com.odc.hub.user.model.User;
import com.odc.hub.user.repository.UserRepository;
import com.odc.hub.common.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanningNotificationService {

    private final NotificationService notificationService;
    private final EmailService emailService;
    private final UserRepository userRepository;

    public void onCreated(PlanningItem item, User actor) {
        notifyUsers(
                item,
                actor,
                NotificationType.PLANNING_ITEM_CREATED,
                "New planning item",
                "\"" + item.getTitle() + "\" has been added to your planning"
        );
    }

    public void onUpdated(PlanningItem item, User actor) {
        notifyUsers(
                item,
                actor,
                NotificationType.PLANNING_ITEM_UPDATED,
                "Planning item updated",
                "\"" + item.getTitle() + "\" has been updated"
        );
    }

    public void onDeleted(PlanningItem item, User actor) {
        notifyUsers(
                item,
                actor,
                NotificationType.PLANNING_ITEM_DELETED,
                "Planning item removed",
                "\"" + item.getTitle() + "\" has been removed from your planning"
        );
    }

    private void notifyUsers(
            PlanningItem item,
            User actor,
            NotificationType type,
            String title,
            String message
    ) {
        if (item.getUserIds() == null) return;

        for (String userId : item.getUserIds()) {

            // 🚫 do not notify the actor
            if (userId.equals(actor.getId())) continue;

            // 🔔 in-app notification
            notificationService.notify(
                    userId,
                    actor.getId(),
                    type,
                    title,
                    message,
                    "PLANNING_ITEM",
                    item.getId()
            );

            // 📧 email (best effort)
            userRepository.findById(userId).ifPresent(user ->
                    emailService.sendPlanningNotificationEmail(
                            user.getEmail(),
                            title,
                            message
                    )
            );
        }
    }
}