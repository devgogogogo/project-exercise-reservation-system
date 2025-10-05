package com.fastcampus.exercisereservationsystem.domain.user.dto.response;

import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;

import java.time.LocalDate;

public record CreateUserResponse(
        String name,
        String username,
        String nickname,
        LocalDate startedAt,
        LocalDate endedAt
) {
    public static CreateUserResponse from(UserEntity userEntity) {
        return new CreateUserResponse(
                userEntity.getName(),
                userEntity.getUsername(),
                userEntity.getNickname(),
                userEntity.getStartAt(),
                userEntity.getEndAt()
        );
    }
}
