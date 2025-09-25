package com.fastcampus.exercisereservationsystem.domain.user.dto.response;

import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;

import java.time.LocalDate;

public record UpdateUserResponse(
        Long id,
        String name,
        String username,
        LocalDate startedAt,
        LocalDate endedAt
) {
    public static  UpdateUserResponse from(UserEntity user) {
        return new UpdateUserResponse(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getStartAt(),
                user.getEndAt()
        );
    }
}
