package com.fastcampus.exercisereservationsystem.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(HttpStatus status, String message) {

    public static ErrorResponse from(ErrorCode errorCode) {
        return new  ErrorResponse(
                errorCode.getStatus(),
                errorCode.getMessage()
        );
    }
}
