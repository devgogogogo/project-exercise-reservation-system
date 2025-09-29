package com.fastcampus.exercisereservationsystem.domain.reservation.controller;

import com.fastcampus.exercisereservationsystem.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/classSchedules/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

}