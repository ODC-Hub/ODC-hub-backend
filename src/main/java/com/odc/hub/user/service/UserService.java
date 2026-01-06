package com.odc.hub.user.service;

import com.odc.hub.audit.service.AuditService;
import com.odc.hub.common.service.EmailService;
import com.odc.hub.user.dto.ProfileResponseDto;
import com.odc.hub.user.dto.UpdateProfileRequest;
import com.odc.hub.user.dto.UserResponseDto;
import com.odc.hub.user.mapper.UserMapper;
import com.odc.hub.user.model.AccountStatus;
import com.odc.hub.user.model.Role;
import com.odc.hub.user.model.User;
import com.odc.hub.user.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final AuditService auditService;

    public UserService(
            UserRepository userRepository,
            EmailService emailService,
            AuditService auditService
    ) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.auditService = auditService;
    }


    public List<UserResponseDto> getPendingUsers() {
        return userRepository.findByStatus(AccountStatus.PENDING)
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    public List<UserResponseDto> searchUsers(
            String email,
            Role role,
            AccountStatus status
    ) {
        List<User> users;

        if (email != null && role != null && status != null) {
            users = userRepository
                    .findByEmailContainingIgnoreCaseAndRoleAndStatus(email, role, status);

        } else if (email != null && role != null) {
            users = userRepository
                    .findByEmailContainingIgnoreCaseAndRole(email, role);

        } else if (email != null && status != null) {
            users = userRepository
                    .findByEmailContainingIgnoreCaseAndStatus(email, status);

        } else if (role != null && status != null) {
            users = userRepository.findByRoleAndStatus(role, status);

        } else if (email != null) {
            users = userRepository.findByEmailContainingIgnoreCase(email);

        } else if (role != null) {
            users = userRepository.findByRole(role);

        } else if (status != null) {
            users = userRepository.findByStatus(status);

        } else {
            users = userRepository.findAll();
        }

        return users.stream()
                .map(UserMapper::toDto)
                .toList();
    }

    public void changeStatus(String userId, AccountStatus status) {

        User actor = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AccountStatus oldStatus = user.getStatus();

        if (user.getRole() == Role.ADMIN && status == AccountStatus.DISABLED) {
            long admins = userRepository.countByRole(Role.ADMIN);
            if (admins <= 1) {
                throw new IllegalStateException("Cannot disable last admin");
            }
        }

        user.setStatus(status);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        auditService.log(
                actor,
                user,
                "CHANGE_STATUS",
                oldStatus.name(),
                status.name()
        );
    }


    public void approveUser(String userId) {

        User actor = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() != AccountStatus.PENDING) {
            throw new IllegalStateException("User is not pending");
        }

        user.setStatus(AccountStatus.APPROVED);

        user.setActivationToken(UUID.randomUUID().toString());
        user.setActivationTokenExpiry(
                Instant.now().plus(24, ChronoUnit.HOURS)
        );

        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        auditService.log(
                actor,
                user,
                "APPROVE_USER",
                "PENDING",
                "APPROVED"
        );

        try {
            emailService.sendActivationEmail(
                    user.getEmail(),
                    user.getActivationToken()
            );
        } catch (Exception e) {
            // rollback approval
            user.setStatus(AccountStatus.PENDING);
            user.setActivationToken(null);
            user.setActivationTokenExpiry(null);
            userRepository.save(user);

            throw new RuntimeException("Email sending failed. Approval rolled back.");
        }
    }


    public void rejectUser(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRole() == Role.ADMIN) {
            long adminCount = userRepository.countByRole(Role.ADMIN);
            if (adminCount <= 1) {
                throw new RuntimeException("Cannot remove last admin");
            }
        }

        userRepository.deleteById(userId);
    }

    public void disableUser(String userId) {

        User actor = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            long adminCount = userRepository.countByRole(Role.ADMIN);
            if (adminCount <= 1) {
                throw new RuntimeException("Cannot disable last admin");
            }
        }

        user.setStatus(AccountStatus.DISABLED);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        auditService.log(
                actor,
                user,
                "DISABLE_USER",
                "ACTIVE",
                "DISABLED"
        );
    }

    public void changeUserRole(String userId, Role newRole) {

        User actor = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role oldRole = user.getRole();

        if (user.getRole() == Role.ADMIN && newRole != Role.ADMIN) {
            long adminCount = userRepository.countByRole(Role.ADMIN);
            if (adminCount <= 1) {
                throw new RuntimeException("Cannot disable last admin");
            }
        }

        user.setRole(newRole);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        auditService.log(
                actor,
                user,
                "CHANGE_ROLE",
                oldRole.name(),
                newRole.name()
        );
    }

    public ProfileResponseDto updateProfile(UpdateProfileRequest request) {

        User user = getCurrentAuthenticatedUser();

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getGithubUrl() != null) {
            user.setGithubUrl(request.getGithubUrl());
        }
        if (request.getLinkedinUrl() != null) {
            user.setLinkedinUrl(request.getLinkedinUrl());
        }

        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        return UserMapper.toProfileDto(user);
    }
    private User getCurrentAuthenticatedUser() {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (!(principal instanceof User user)) {
            throw new IllegalStateException("Invalid authentication principal");
        }

        return user;
    }

    public ProfileResponseDto getCurrentProfile() {
        User user = getCurrentAuthenticatedUser();
        return UserMapper.toProfileDto(user);
    }

}
