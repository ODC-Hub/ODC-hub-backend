package com.odc.hub.auth.service;

import com.odc.hub.audit.service.AuditService;
import com.odc.hub.auth.dto.LoginRequest;
import com.odc.hub.common.service.EmailService;
import com.odc.hub.user.model.AccountStatus;
import com.odc.hub.user.model.Role;
import com.odc.hub.user.model.User;
import com.odc.hub.user.repository.UserRepository;
import com.odc.hub.auth.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User activeUser;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        activeUser = new User();
        activeUser.setId("123");
        activeUser.setEmail("user@test.com");
        activeUser.setPassword("hashed");
        activeUser.setRole(Role.BOOTCAMPER);
        activeUser.setStatus(AccountStatus.ACTIVE);
    }

    @Test
    void loginShouldFailWhenUserNotFound() {
        when(userRepository.findByEmail("x@test.com")).thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest();
        request.setEmail("x@test.com");
        request.setPassword("pwd");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.login(request, mock(HttpServletResponse.class)));

        assertEquals("Invalid credentials", ex.getMessage());

    }


    @Test
    void loginShouldFailWhenPasswordIsWrong() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("bad", "hashed")).thenReturn(false);

        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("bad");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.login(request, mock(HttpServletResponse.class)));

        assertTrue(ex.getMessage().contains("Invalid email or password"));

    }

    @Test
    void loginShouldFailWhenAccountIsPending() {
        activeUser.setStatus(AccountStatus.PENDING);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("pwd", "hashed")).thenReturn(true);

        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("pwd");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.login(request, null));

        assertEquals("Account not active", ex.getMessage());
    }

    @Test
    void loginShouldFailWhenAccountIsDisabled() {
        activeUser.setStatus(AccountStatus.DISABLED);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("pwd", "hashed")).thenReturn(true);

        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("pwd");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.login(request, null));

        assertEquals("Account not active", ex.getMessage());
    }

    @Test
    void loginShouldSucceedForActiveUser() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("pwd", "hashed")).thenReturn(true);
        when(jwtService.generateAccessToken(activeUser)).thenReturn("access");
        when(jwtService.generateRefreshToken(activeUser)).thenReturn("refresh");

        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("pwd");

        authService.login(request, null);

        verify(jwtService).generateAccessToken(activeUser);
        verify(jwtService).generateRefreshToken(activeUser);
    }

    @Test
    void loginShouldCreateCookies() {
        UserRepository repo = Mockito.mock(UserRepository.class);
        com.odc.hub.auth.service.JwtService jwtService = new com.odc.hub.auth.service.JwtService(
                "super-secret-key-super-secret-key-super-secret-key",
                60000,
                3600000
        );

        User user = new User();
        user.setId("1");
        user.setEmail("test@test.com");
        user.setRole(Role.ADMIN);
        user.setStatus(AccountStatus.ACTIVE);
        user.setPassword(new BCryptPasswordEncoder().encode("1234"));

        Mockito.when(repo.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        AuthService authService = new AuthService(
                repo,
                new BCryptPasswordEncoder(),
                jwtService,
                Mockito.mock(EmailService.class),
                Mockito.mock(AuditService.class)
        );

        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("1234");
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        authService.login(request, response);

        verify(response, Mockito.atLeastOnce()).addCookie(Mockito.any());
    }
}
