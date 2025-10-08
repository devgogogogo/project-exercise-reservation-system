package com.fastcampus.exercisereservationsystem.domain.program.dto.response;

import com.fastcampus.exercisereservationsystem.domain.program.entity.ProgramEntity;

import java.time.LocalDate;

public record CreateProgramResponse(
        Long id,
        String exerciseName,
        String exerciseDescription,
        LocalDate date
) {

    public static CreateProgramResponse from(ProgramEntity entity) {
        return new CreateProgramResponse(
                entity.getId(),
                entity.getExerciseName(),
                entity.getExerciseDescription(),
                entity.getDate()
        );
    }
}
