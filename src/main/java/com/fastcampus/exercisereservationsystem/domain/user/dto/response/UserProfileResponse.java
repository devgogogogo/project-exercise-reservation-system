package com.fastcampus.exercisereservationsystem.domain.user.dto.response;

import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;

public record UserProfileResponse(
        Long id,
        String username,
        String name,
        String nickname,
        String role
) {
    public static UserProfileResponse from(UserEntity user) {
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getNickname(),
                user.getRole().name()
        );
    }
}
