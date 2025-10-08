package com.fastcampus.exercisereservationsystem.domain.program.dto.response;

import com.fastcampus.exercisereservationsystem.domain.program.entity.ProgramEntity;

import java.time.LocalDate;

public record UpdateProgramResponse(
        String exerciseName,
        String exerciseDescription,
        LocalDate date
) {
    public static UpdateProgramResponse from(ProgramEntity entity) {
        return new UpdateProgramResponse(
                entity.getExerciseName(),
                entity.getExerciseDescription(),
                entity.getDate()
        );
    }
}
