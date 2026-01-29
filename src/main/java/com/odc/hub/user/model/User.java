package com.odc.hub.user.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

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

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public ObjectId getAvatarFileId() {
        return avatarFileId;
    }

    public void setAvatarFileId(ObjectId avatarFileId) {
        this.avatarFileId = avatarFileId;
    }

    public String getAvatarContentType() {
        return avatarContentType;
    }

    public void setAvatarContentType(String avatarContentType) {
        this.avatarContentType = avatarContentType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getActivationToken() {
        return activationToken;
    }

    public void setActivationToken(String activationToken) {
        this.activationToken = activationToken;
    }

    public Instant getActivationTokenExpiry() {
        return activationTokenExpiry;
    }

    public void setActivationTokenExpiry(Instant activationTokenExpiry) {
        this.activationTokenExpiry = activationTokenExpiry;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public Instant getResetPasswordTokenExpiry() {
        return resetPasswordTokenExpiry;
    }

    public void setResetPasswordTokenExpiry(Instant resetPasswordTokenExpiry) {
        this.resetPasswordTokenExpiry = resetPasswordTokenExpiry;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public Instant getLastFailedLoginAt() {
        return lastFailedLoginAt;
    }

    public void setLastFailedLoginAt(Instant lastFailedLoginAt) {
        this.lastFailedLoginAt = lastFailedLoginAt;
    }

    public Instant getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(Instant lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    public Instant getLastPasswordResetRequestAt() {
        return lastPasswordResetRequestAt;
    }

    public void setLastPasswordResetRequestAt(Instant lastPasswordResetRequestAt) {
        this.lastPasswordResetRequestAt = lastPasswordResetRequestAt;
    }

    public int getPasswordResetRequestCount() {
        return passwordResetRequestCount;
    }

    public void setPasswordResetRequestCount(int passwordResetRequestCount) {
        this.passwordResetRequestCount = passwordResetRequestCount;
    }

    public User(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = AccountStatus.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();

    }
}
