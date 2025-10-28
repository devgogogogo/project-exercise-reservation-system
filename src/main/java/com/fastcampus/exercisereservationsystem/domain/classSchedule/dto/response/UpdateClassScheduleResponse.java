package com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.response;

import com.fastcampus.exercisereservationsystem.domain.classSchedule.entity.ClassScheduleEntity;

import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateClassScheduleResponse(
        String classname,
        String description,
        LocalTime startTime,
        LocalTime endTime,
        LocalDate date,
        Integer capacity
) {
    public static UpdateClassScheduleResponse from(ClassScheduleEntity entity) {
        return new UpdateClassScheduleResponse(
                entity.getClassName(),
                entity.getDescription(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getDate(),
                entity.getCapacity()
        );
    }
}
