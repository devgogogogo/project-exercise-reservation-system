package com.fastcampus.exercisereservationsystem.domain.reservation.controller;

import com.fastcampus.exercisereservationsystem.domain.reservation.dto.response.GetReservationListResponse;
import com.fastcampus.exercisereservationsystem.domain.reservation.dto.response.ReservationResponse;
import com.fastcampus.exercisereservationsystem.domain.reservation.service.ReservationService;
import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/class-schedules/{scheduleId}/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    //예약
    @PostMapping
    public ResponseEntity<ReservationResponse> reservation(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal UserEntity userEntity
    ) {
        ReservationResponse response = reservationService.reservation(scheduleId, userEntity);
        return ResponseEntity.ok(response);
    }

    //예약한 사람들 조회
    @GetMapping
    public ResponseEntity<List<GetReservationListResponse>> getReservationList(
            @PathVariable Long scheduleId
    ) {
        List<GetReservationListResponse> responses = reservationService.getReservationList(scheduleId);
        return ResponseEntity.ok(responses);
    }


    //예약 취소
    @PostMapping
    public ResponseEntity<ReservationResponse> cancelReservation(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal UserEntity userEntity
    ) {
        ReservationResponse response = reservationService.cancelReservation(scheduleId, userEntity);
        return ResponseEntity.ok(response);
    }
}