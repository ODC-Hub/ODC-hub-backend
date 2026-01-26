package com.odc.hub.auth.service;

import com.odc.hub.audit.service.AuditService;
import com.odc.hub.auth.dto.ChangePasswordRequest;
import com.odc.hub.auth.dto.LoginRequest;
import com.odc.hub.auth.dto.RegisterRequest;
import com.odc.hub.auth.dto.ResetPasswordRequest;
import com.odc.hub.common.service.EmailService;
import com.odc.hub.user.model.AccountStatus;
import com.odc.hub.user.model.Role;
import com.odc.hub.user.model.User;
import com.odc.hub.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final AuditService auditService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
            EmailService emailService, AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.auditService = auditService;
    }

    private static final int MAX_ATTEMPTS = 5;
    private static final int ATTEMPT_WINDOW_MINUTES = 1;
    private static final int LOCK_DURATION_MINUTES = 15;

    private static final int RESET_WINDOW_MINUTES = 15;
    private static final int MAX_RESET_REQUESTS = 3;

    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (request.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("You cannot register as ADMIN");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        user.setStatus(AccountStatus.PENDING);

        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        userRepository.save(user);
    }

    public void activateAccount(String token) {

        User user = userRepository.findByActivationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (user.getActivationTokenExpiry().isBefore(Instant.now())) {
            throw new RuntimeException("Token expired");
        }

        user.setStatus(AccountStatus.ACTIVE);
        user.setActivationToken(null);
        user.setActivationTokenExpiry(null);
        user.setUpdatedAt(Instant.now());

        userRepository.save(user);
    }

    public void login(LoginRequest request, HttpServletResponse response) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // Check if account is locked
        if (user.getLockedUntil() != null &&
                user.getLockedUntil().isAfter(Instant.now())) {

            throw new ResponseStatusException(
                    HttpStatus.LOCKED,
                    "Account locked. Try again later.");
        }

        // Check ACTIVE
        if (user.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Account not active");
        }

        // Password check
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            handleFailedLogin(user);
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password");
        }

        // SUCCESS → reset security counters
        resetFailedLogins(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // 🚨 Only touch cookies if HTTP layer exists
        if (response != null) {
            Cookie accessCookie = new Cookie("access_token", accessToken);
            accessCookie.setHttpOnly(true);
            accessCookie.setPath("/");
            accessCookie.setMaxAge((int) (jwtService.getAccessExpiration() / 1000));

            Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge((int) (jwtService.getRefreshExpiration() / 1000));

            response.addCookie(accessCookie);
            response.addCookie(refreshCookie);
        }
    }

    private void handleFailedLogin(User user) {

        Instant now = Instant.now();

        boolean wasAlreadyLocked = user.getLockedUntil() != null &&
                user.getLockedUntil().isAfter(now);

        // If last failure was too old → reset window
        if (user.getLastFailedLoginAt() == null ||
                user.getLastFailedLoginAt().isBefore(now.minusSeconds(ATTEMPT_WINDOW_MINUTES * 60))) {

            user.setFailedLoginAttempts(1);
        } else {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        }

        user.setLastFailedLoginAt(now);

        // Lock account if threshold reached
        if (user.getFailedLoginAttempts() >= MAX_ATTEMPTS && !wasAlreadyLocked) {
            user.setLockedUntil(now.plusSeconds(LOCK_DURATION_MINUTES * 60));

            auditService.log(
                    user,
                    user,
                    "ACCOUNT_LOCKED",
                    "ACTIVE",
                    "DISABLED");
        }

        userRepository.save(user);

    }

    private void resetFailedLogins(User user) {
        user.setFailedLoginAttempts(0);
        user.setLastFailedLoginAt(null);
        user.setLockedUntil(null);
        userRepository.save(user);
    }

    public void refresh(String refreshToken, HttpServletResponse response) {

        try {
            Claims claims = jwtService.extractClaims(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        } catch (JwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        String userId = jwtService.extractClaims(refreshToken).getSubject();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Account disabled");
        }

        String newAccessToken = jwtService.generateAccessToken(user);

        Cookie accessCookie = new Cookie("access_token", newAccessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge((int) (jwtService.getAccessExpiration() / 1000));

        response.addCookie(accessCookie);
    }

    public void forgotPassword(String email) {

        // User user = userRepository.findByEmail(email)
        // .orElseThrow(() -> new RuntimeException("User return;not found"));

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return; // pretend email was sent
        }

        Instant now = Instant.now();

        if (user.getLastPasswordResetRequestAt() != null &&
                user.getLastPasswordResetRequestAt()
                        .isAfter(now.minusSeconds(RESET_WINDOW_MINUTES * 60))) {

            if (user.getPasswordResetRequestCount() >= MAX_RESET_REQUESTS) {
                throw new RuntimeException(
                        "Too many reset requests. Try later.");
            }

            user.setPasswordResetRequestCount(
                    user.getPasswordResetRequestCount() + 1);
        } else {
            user.setPasswordResetRequestCount(1);
        }

        user.setLastPasswordResetRequestAt(now);

        String rawToken = UUID.randomUUID().toString();

        user.setResetPasswordToken(passwordEncoder.encode(rawToken));
        user.setResetPasswordTokenExpiry(
                now.plus(15, ChronoUnit.MINUTES));

        userRepository.save(user);

        emailService.sendPasswordResetEmail(
                user.getEmail(),
                rawToken);
    }

    public void resetPassword(ResetPasswordRequest request) {

        User user = userRepository.findAll().stream()
                .filter(u -> u.getResetPasswordToken() != null &&
                        passwordEncoder.matches(
                                request.getToken(),
                                u.getResetPasswordToken()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (user.getResetPasswordTokenExpiry().isBefore(Instant.now())) {
            throw new RuntimeException("Token expired");
        }

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword()));

        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);
        user.setUpdatedAt(Instant.now());

        userRepository.save(user);

        auditService.log(
                user,
                user,
                "CHANGE_PASSWORD",
                "****",
                "****");

    }

    public void changePassword(ChangePasswordRequest request) {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(Instant.now());

        userRepository.save(user);
    }

}
