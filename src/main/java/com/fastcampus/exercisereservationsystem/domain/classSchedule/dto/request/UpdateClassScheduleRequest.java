package com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateClassScheduleRequest(
        String className,
        String description,
        LocalTime startTime,
        LocalTime endTime,
        LocalDate date,
        Integer capacity
) {
}
