package com.fastcampus.exercisereservationsystem.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


public record CreateUserRequest(
        @NotBlank(message = "제대로 입력하세요")
        String name,

        @NotBlank(message = "닉네임을 입력하세요.")
        String nickname,

        @Email(message = "이메일은 필수값입니다.")
        String username,

        @Size(min = 8, message = "비밀번호는 8자 이상")
        String password,

        @NotNull(message = "시작일을 선택하세요.")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startAt,

        @NotNull(message = "종료일을 선택하세요.")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endAt) {
}
