package com.fastcampus.exercisereservationsystem.domain.program.dto.request;


import java.time.LocalDate;

public record CreateProgramRequest(
        String exerciseName,
        String exerciseDescription,
        LocalDate date
) {
}
