package com.fastcampus.exercisereservationsystem.domain.user.exception;

import com.fastcampus.exercisereservationsystem.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "유저가 존재하지 않습니다."),
    USER_ALREADY_EXISTED(HttpStatus.CONFLICT, "U002", "유저가 이미 존재합니다."),
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "U003", "로그인이 필요합니다."),
    USER_FORBIDDEN(HttpStatus.FORBIDDEN, "U004", "관리자만 가능합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
