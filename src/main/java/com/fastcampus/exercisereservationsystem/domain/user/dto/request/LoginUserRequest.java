package com.fastcampus.exercisereservationsystem.domain.user.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record LoginUserRequest(
        @NotEmpty
        String username,
        @NotEmpty
        String password
) {
}
