package com.odc.hub.user.dto;

import com.odc.hub.user.model.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class ProfileResponseDto {
    private String email;
    private String fullName;
    private Role role;
    private String phone;
    private String githubUrl;
    private String linkedinUrl;
    private String avatarFileId;
    private String bio;

}

