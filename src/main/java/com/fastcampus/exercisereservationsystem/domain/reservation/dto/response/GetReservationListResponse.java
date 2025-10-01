package com.fastcampus.exercisereservationsystem.domain.reservation.dto.response;

import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;

public record GetReservationListResponse(
        String name,
        String username
) {
    public static GetReservationListResponse from(UserEntity entity) {
        return new GetReservationListResponse(
                entity.getName(),
                entity.getUsername()
        );
    }
}
