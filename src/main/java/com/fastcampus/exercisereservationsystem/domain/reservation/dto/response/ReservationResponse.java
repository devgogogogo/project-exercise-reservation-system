package com.fastcampus.exercisereservationsystem.domain.reservation.dto.response;

import com.fastcampus.exercisereservationsystem.domain.reservation.entity.ReservationEntity;

public record ReservationResponse(
        Long reservationId, //생성 되었거나 , 취소된 예약의 id
        Long scheduleId,
        String className,
        Long reservedCount //현재 예약 인원 수

) {
    public static ReservationResponse from(ReservationEntity entity,Long reservedCount) {
        return new ReservationResponse(
                entity.getId(),
                entity.getClassSchedule().getId(),
                entity.getClassSchedule().getClassName(),
                reservedCount
        );
    }
}
