package com.odc.hub.user.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor

public class UpdateProfileRequest {
    private String fullName;
    private String bio;


    @Pattern(
            regexp = "^\\+?[0-9 ]{8,15}$",
            message = "Invalid phone number"
    )
    private String phone;

    @Pattern(
            regexp = "^https://(www\\.)?github\\.com/.+$",
            message = "Invalid GitHub URL"
    )
    private String githubUrl;

    @Pattern(
            regexp = "^https://(www\\.)?linkedin\\.com/in/.+$",
            message = "Invalid LinkedIn URL"
    )
    private String linkedinUrl;
}