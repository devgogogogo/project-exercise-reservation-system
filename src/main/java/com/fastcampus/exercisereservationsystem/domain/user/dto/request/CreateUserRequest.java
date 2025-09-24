package com.fastcampus.exercisereservationsystem.domain.user.dto.request;

import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;

import java.time.LocalDate;

public record CreateUserRequest(
        String name,
        String username,
        String password,
        LocalDate startedAt,
        LocalDate endedAt
) {
}
