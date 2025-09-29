package com.fastcampus.exercisereservationsystem.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(BizException.class)
    public ResponseEntity<ErrorResponse> handleClientErrorException(BizException e) {

        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(new ErrorResponse(e.getErrorCode().getStatus(),e.getErrorCode().getCode(),e.getMessage()));

    }
}
