package com.odc.hub.user.mapper;

import com.odc.hub.user.dto.ProfileResponseDto;
import com.odc.hub.user.dto.UserResponseDto;
import com.odc.hub.user.model.User;

public class UserMapper {

    public static UserResponseDto toDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .fullName(user.getFullName())
                .avatarFileId(
                        user.getAvatarFileId() != null
                                ? user.getAvatarFileId().toHexString()
                                : null
                )
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static ProfileResponseDto toProfileDto(User user) {
        return ProfileResponseDto.builder()
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .bio(user.getBio())
                .githubUrl(user.getGithubUrl())
                .linkedinUrl(user.getLinkedinUrl())
                .role(user.getRole())
                .avatarFileId(
                        user.getAvatarFileId() != null
                                ? user.getAvatarFileId().toHexString()
                                : null
                )
                .build();
    }


}
