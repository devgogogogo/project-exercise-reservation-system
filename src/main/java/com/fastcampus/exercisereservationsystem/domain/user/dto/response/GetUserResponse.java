package com.fastcampus.exercisereservationsystem.domain.user.dto.response;

import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;

import java.time.LocalDate;

public record GetUserResponse(
        Long id,
        String name,
        String nickname,
        String username,
        LocalDate startedAt,
        LocalDate endedAt
) {
    public static GetUserResponse from(UserEntity user) {
        return new GetUserResponse(
                user.getId(),
                user.getName(),
                user.getNickname(),
                user.getUsername(),
                user.getStartAt(),
                user.getEndAt()
        );
    }
}
