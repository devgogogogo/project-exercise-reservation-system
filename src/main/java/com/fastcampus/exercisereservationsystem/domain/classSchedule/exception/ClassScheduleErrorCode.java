package com.fastcampus.exercisereservationsystem.domain.classSchedule.exception;

import com.fastcampus.exercisereservationsystem.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ClassScheduleErrorCode implements ErrorCode {
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "수업 스케쥴이 존재하지 않습니다."),
    SCHEDULE_ALREADY_EXISTED(HttpStatus.CONFLICT, "S002", "수업 스케줄이 이미 존재합니다.");


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
