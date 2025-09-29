package com.fastcampus.exercisereservationsystem.domain.reservation.exception;

import com.fastcampus.exercisereservationsystem.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ReservationErrorCode implements ErrorCode {
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "예약이 존재하지 않습니다."),
    RESERVATION_ALREADY_EXISTED(HttpStatus.CONFLICT, "R002", "예약이 이미 존재합니다.");


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
