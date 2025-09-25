package com.fastcampus.exercisereservationsystem.domain.user.dto.request;

import java.time.LocalDate;

public record UpdateUserRequest(
        LocalDate startAt,
        LocalDate endAt
) {
}
