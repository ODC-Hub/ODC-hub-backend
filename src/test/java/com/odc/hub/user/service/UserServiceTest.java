package com.odc.hub.user.service;

import com.odc.hub.audit.service.AuditService;
import com.odc.hub.common.service.EmailService;
import com.odc.hub.user.model.AccountStatus;
import com.odc.hub.user.model.Role;
import com.odc.hub.user.model.User;
import com.odc.hub.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class UserServiceTest {

    @Test
    void approveUserShouldSetStatusAndToken() {
        UserRepository repo = Mockito.mock(UserRepository.class);
        EmailService emailService = Mockito.mock(EmailService.class);
        AuditService auditService = Mockito.mock(AuditService.class);

        User user = new User();
        user.setId("1");
        user.setEmail("u@test.com");
        user.setStatus(AccountStatus.PENDING);
        user.setRole(Role.BOOTCAMPER);

        Mockito.when(repo.findById("1")).thenReturn(Optional.of(user));

        UserService service = new UserService(repo, emailService, auditService);

        service.approveUser("1");

        assertThat(user.getStatus()).isEqualTo(AccountStatus.APPROVED);
        assertThat(user.getActivationToken()).isNotNull();
    }
}
