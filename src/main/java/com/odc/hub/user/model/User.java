package com.odc.hub.user.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String fullName;
    private String phone;

    @Indexed(unique = true)
    private String email;

    private String password;

    private Role role;

    private AccountStatus status;

    private String githubUrl;
    private String linkedinUrl;

    private String promotionName;

    private String bio;

    private ObjectId avatarFileId;

    private String avatarContentType;

    private Instant createdAt;

    private Instant updatedAt;

    private String activationToken;
    private Instant activationTokenExpiry;

    private String resetPasswordToken;
    private Instant resetPasswordTokenExpiry;

    private int failedLoginAttempts = 0;
    private Instant lastFailedLoginAt;
    private Instant lockedUntil;

    private Instant lastPasswordResetRequestAt;
    private int passwordResetRequestCount;


    public User(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = AccountStatus.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();

    }
}
