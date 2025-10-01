package com.fastcampus.exercisereservationsystem.domain.reservation.dto.response;

import com.fastcampus.exercisereservationsystem.domain.reservation.entity.ReservationEntity;

public record ReservationResponse(
        Long reservationId,
        Long scheduleId,
        String className,
        int reservedCount

) {
    public static ReservationResponse from(ReservationEntity entity,int reservedCount) {
        return new ReservationResponse(
                entity.getId(),
                entity.getClassSchedule().getId(),
                entity.getClassSchedule().getClassName(),
                reservedCount
        );
    }
}
