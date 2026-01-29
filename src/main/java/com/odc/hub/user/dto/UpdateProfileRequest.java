package com.odc.hub.user.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class UpdateProfileRequest {
        public UpdateProfileRequest() {
        }

        public UpdateProfileRequest(String fullName, String bio, String phone, String githubUrl, String linkedinUrl) {
                this.fullName = fullName;
                this.bio = bio;
                this.phone = phone;
                this.githubUrl = githubUrl;
                this.linkedinUrl = linkedinUrl;
        }

        public String getFullName() {
                return fullName;
        }

        public void setFullName(String fullName) {
                this.fullName = fullName;
        }

        public String getBio() {
                return bio;
        }

        public void setBio(String bio) {
                this.bio = bio;
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

        private String fullName;
        private String bio;

        @Pattern(regexp = "^\\+?[0-9 ]{8,15}$", message = "Invalid phone number")
        private String phone;

        @Pattern(regexp = "^https://(www\\.)?github\\.com/.+$", message = "Invalid GitHub URL")
        private String githubUrl;

        @Pattern(regexp = "^https://(www\\.)?linkedin\\.com/in/.+$", message = "Invalid LinkedIn URL")
        private String linkedinUrl;
}