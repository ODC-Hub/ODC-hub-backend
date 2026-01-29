package com.odc.hub.user.dto;

import com.odc.hub.user.model.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class ProfileResponseDto {
    private String email;
    private String fullName;
    private Role role;
    private String phone;
    private String githubUrl;
    private String linkedinUrl;
    private String avatarFileId;
    private String bio;

    public ProfileResponseDto() {
    }

    public ProfileResponseDto(String email, String fullName, Role role, String phone, String githubUrl,
            String linkedinUrl, String avatarFileId, String bio) {
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.phone = phone;
        this.githubUrl = githubUrl;
        this.linkedinUrl = linkedinUrl;
        this.avatarFileId = avatarFileId;
        this.bio = bio;
    }

    public static ProfileResponseDtoBuilder builder() {
        return new ProfileResponseDtoBuilder();
    }

    public static class ProfileResponseDtoBuilder {
        private String email;
        private String fullName;
        private Role role;
        private String phone;
        private String githubUrl;
        private String linkedinUrl;
        private String avatarFileId;
        private String bio;

        public ProfileResponseDtoBuilder email(String email) {
            this.email = email;
            return this;
        }

        public ProfileResponseDtoBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public ProfileResponseDtoBuilder role(Role role) {
            this.role = role;
            return this;
        }

        public ProfileResponseDtoBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public ProfileResponseDtoBuilder githubUrl(String githubUrl) {
            this.githubUrl = githubUrl;
            return this;
        }

        public ProfileResponseDtoBuilder linkedinUrl(String linkedinUrl) {
            this.linkedinUrl = linkedinUrl;
            return this;
        }

        public ProfileResponseDtoBuilder avatarFileId(String avatarFileId) {
            this.avatarFileId = avatarFileId;
            return this;
        }

        public ProfileResponseDtoBuilder bio(String bio) {
            this.bio = bio;
            return this;
        }

        public ProfileResponseDto build() {
            return new ProfileResponseDto(email, fullName, role, phone, githubUrl, linkedinUrl, avatarFileId, bio);
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getAvatarFileId() {
        return avatarFileId;
    }

    public void setAvatarFileId(String avatarFileId) {
        this.avatarFileId = avatarFileId;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
