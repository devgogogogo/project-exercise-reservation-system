package com.fastcampus.exercisereservationsystem.common.dto.request;

import jakarta.validation.constraints.NotNull;

public record LoginRequestDto(
        @NotNull(message = "이메일 입력은 필수 입니다.") String email,
        @NotNull(message = "패스워드 입력은 필수입니다.") String password
) {
}
