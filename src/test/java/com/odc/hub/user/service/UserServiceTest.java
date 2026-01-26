package com.odc.hub.user.service;

import com.odc.hub.audit.service.AuditService;
import com.odc.hub.common.service.EmailService;
import com.odc.hub.user.model.AccountStatus;
import com.odc.hub.user.model.Role;
import com.odc.hub.user.model.User;
import com.odc.hub.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserServiceTest {

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void approveUser_shouldApproveAndSendEmail() {
        // ===== mocks =====
        UserRepository repo = Mockito.mock(UserRepository.class);
        EmailService emailService = Mockito.mock(EmailService.class);
        AuditService auditService = Mockito.mock(AuditService.class);

        UserService service = new UserService(repo, emailService, auditService);

        // ===== authenticated admin (actor) =====
        User admin = new User();
        admin.setId("admin-id");
        admin.setEmail("admin@test.com");
        admin.setRole(Role.ADMIN);
        admin.setStatus(AccountStatus.ACTIVE);

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        Mockito.when(authentication.getPrincipal()).thenReturn(admin);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // ===== pending user =====
        User pendingUser = new User();
        pendingUser.setId("1");
        pendingUser.setEmail("user@test.com");
        pendingUser.setStatus(AccountStatus.PENDING);
        pendingUser.setRole(Role.BOOTCAMPER);

        Mockito.when(repo.findById("1"))
                .thenReturn(Optional.of(pendingUser));

        // ===== action =====
        service.approveUser("1");

        // ===== assertions =====
        assertThat(pendingUser.getStatus()).isEqualTo(AccountStatus.APPROVED);
        assertThat(pendingUser.getActivationToken()).isNotNull();
        assertThat(pendingUser.getActivationTokenExpiry()).isNotNull();

        // ===== verifications =====
        Mockito.verify(repo).save(pendingUser);

        Mockito.verify(auditService).log(
                Mockito.eq(admin),
                Mockito.eq(pendingUser),
                Mockito.eq("APPROVE_USER"),
                Mockito.eq("PENDING"),
                Mockito.eq("APPROVED")
        );

        Mockito.verify(emailService)
                .sendActivationEmail(
                        Mockito.eq(pendingUser.getEmail()),
                        Mockito.eq(pendingUser.getActivationToken())
                );
    }
}
