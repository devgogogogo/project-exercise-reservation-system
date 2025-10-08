package com.fastcampus.exercisereservationsystem.domain.program.exception;

import com.fastcampus.exercisereservationsystem.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ProgramErrorCode implements ErrorCode {
    PROGRAM_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "프로그램이 존재하지 않습니다.");



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
