package com.fastcampus.exercisereservationsystem.domain.program.dto.response;

import com.fastcampus.exercisereservationsystem.domain.program.entity.ProgramEntity;

public record GetByDateProgramResponse(
        Long id,
        String exerciseName,
        String exerciseDescription
) {

    public static GetByDateProgramResponse from(ProgramEntity entity) {
        return new GetByDateProgramResponse(
                entity.getId(),
                entity.getExerciseName(),
                entity.getExerciseDescription()
        );
    }
}
