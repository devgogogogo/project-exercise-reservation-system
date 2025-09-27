package com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.response;

import com.fastcampus.exercisereservationsystem.domain.classSchedule.entity.ClassScheduleEntity;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateClassScheduleResponse(
        String classname,
        LocalTime startTime,
        LocalTime endTime,
        LocalDate date,
        Integer capacity

) {
    public static CreateClassScheduleResponse from(ClassScheduleEntity entity) {
        return new CreateClassScheduleResponse(
                entity.getClassName(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getDate(),
                entity.getCapacity()
        );
    }
}
