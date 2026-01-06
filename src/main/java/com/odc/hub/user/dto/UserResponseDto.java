package com.odc.hub.user.dto;

import com.odc.hub.user.model.AccountStatus;
import com.odc.hub.user.model.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class UserResponseDto {

    private String id;
    private String email;
    private Role role;
    private String fullName;
    private AccountStatus status;
    private String avatarFileId;
    private Instant createdAt;
    private Instant updatedAt;
}
