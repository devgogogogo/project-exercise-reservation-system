package com.fastcampus.exercisereservationsystem.domain.user.dto.response;

public record GetUserPeriodResponse(
        Long remainingPeriod
) {
    public static GetUserPeriodResponse from(Long remainingPeriod) {
        return new GetUserPeriodResponse(remainingPeriod);
    }
}
