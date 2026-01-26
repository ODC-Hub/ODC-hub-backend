package com.odc.hub.user.dto;

import com.odc.hub.user.model.AccountStatus;
import com.odc.hub.user.model.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

public class UserResponseDto {

    private String id;
    private String email;
    private Role role;
    private String fullName;
    private AccountStatus status;
    private String avatarFileId;
    private Instant createdAt;
    private Instant updatedAt;

    public UserResponseDto() {
    }

    public UserResponseDto(String id, String email, Role role, String fullName, AccountStatus status,
            String avatarFileId, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.fullName = fullName;
        this.status = status;
        this.avatarFileId = avatarFileId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserResponseDtoBuilder builder() {
        return new UserResponseDtoBuilder();
    }

    public static class UserResponseDtoBuilder {
        private String id;
        private String email;
        private Role role;
        private String fullName;
        private AccountStatus status;
        private String avatarFileId;
        private Instant createdAt;
        private Instant updatedAt;

        public UserResponseDtoBuilder id(String id) {
            this.id = id;
            return this;
        }

        public UserResponseDtoBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserResponseDtoBuilder role(Role role) {
            this.role = role;
            return this;
        }

        public UserResponseDtoBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public UserResponseDtoBuilder status(AccountStatus status) {
            this.status = status;
            return this;
        }

        public UserResponseDtoBuilder avatarFileId(String avatarFileId) {
            this.avatarFileId = avatarFileId;
            return this;
        }

        public UserResponseDtoBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserResponseDtoBuilder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public UserResponseDto build() {
            return new UserResponseDto(id, email, role, fullName, status, avatarFileId, createdAt, updatedAt);
        }
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public String getFullName() {
        return fullName;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public String getAvatarFileId() {
        return avatarFileId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
