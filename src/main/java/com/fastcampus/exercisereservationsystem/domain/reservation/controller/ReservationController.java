package com.fastcampus.exercisereservationsystem.domain.reservation.controller;

import com.fastcampus.exercisereservationsystem.domain.reservation.dto.response.ReservationResponse;
import com.fastcampus.exercisereservationsystem.domain.reservation.service.ReservationService;
import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/class-schedules/{scheduleId}/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> reservation(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal UserEntity userEntity
    ) {
        ReservationResponse response = reservationService.reservation(scheduleId, userEntity);
        return ResponseEntity.ok(response);
    }

}