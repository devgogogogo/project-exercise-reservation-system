package com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.response;

import com.fastcampus.exercisereservationsystem.domain.classSchedule.entity.ClassScheduleEntity;

import java.time.LocalDate;
import java.time.LocalTime;

public record GetClassScheduleResponse(
        Long id,
        String className,
        String description,
        LocalTime startAt,
        LocalTime endAt,
        LocalDate date,
        Integer capacity
) {
    public static GetClassScheduleResponse from(ClassScheduleEntity entity) {
        return new GetClassScheduleResponse(
                entity.getId(),
                entity.getClassName(),
                entity.getDescription(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getDate(),
                entity.getCapacity()
        );
    }
}
