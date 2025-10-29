package com.fastcampus.exercisereservationsystem.domain.program.dto.response;

import com.fastcampus.exercisereservationsystem.domain.program.entity.ProgramEntity;

public record GetByIdProgramResponse(
        String exerciseName,
        String exerciseDescription
) {
    public static GetByIdProgramResponse from(ProgramEntity entity) {
        return new GetByIdProgramResponse(
                entity.getExerciseName(),
                entity.getExerciseDescription()
        );
    }
}

