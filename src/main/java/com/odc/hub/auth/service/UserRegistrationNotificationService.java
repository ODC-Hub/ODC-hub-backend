package com.odc.hub.auth.service;

import com.odc.hub.common.service.EmailService;
import com.odc.hub.notification.model.NotificationType;
import com.odc.hub.notification.service.NotificationService;
import com.odc.hub.user.model.Role;
import com.odc.hub.user.model.User;
import com.odc.hub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegistrationNotificationService {

    private final NotificationService notificationService;
    private final EmailService emailService;
    private final UserRepository userRepository;

    public void notifyAdmins(User newUser) {

        userRepository.findByRole(Role.ADMIN).forEach(admin -> {

            // 1️⃣ In-app notification
            notificationService.notify(
                    admin.getId(),
                    newUser.getId(), // actor = registering user
                    NotificationType.USER_REGISTRATION_REQUESTED,
                    "New registration request",
                    newUser.getEmail() + " requested access",
                    newUser.getId(),
                    "USER"
            );

            // 2️⃣ Email
            emailService.sendAdminRegistrationRequestEmail(
                    admin.getEmail(),
                    newUser.getEmail(),
                    "http://localhost:5173/admin/users/pending"
            );
        });
    }
}
