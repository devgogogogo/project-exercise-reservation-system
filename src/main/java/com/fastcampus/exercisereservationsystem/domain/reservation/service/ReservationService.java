package com.fastcampus.exercisereservationsystem.domain.reservation.service;

import com.fastcampus.exercisereservationsystem.domain.classSchedule.repository.ClassScheduleRepository;
import com.fastcampus.exercisereservationsystem.domain.reservation.dto.response.ReservationResponse;
import com.fastcampus.exercisereservationsystem.domain.reservation.entity.ReservationEntity;
import com.fastcampus.exercisereservationsystem.domain.reservation.repository.ReservationRepository;
import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ClassScheduleRepository classScheduleRepository;

    public ReservationResponse reservation(Long scheduleId, UserEntity userEntity) {


        new ReservationEntity();
    }
}
