package com.fastcampus.exercisereservationsystem.domain.reservation.repository;

import com.fastcampus.exercisereservationsystem.domain.reservation.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity,Long> {

    boolean existsByClassSchedule_IdAndUser_Id(Long scheduleId, Long userId);

    Long countByClassSchedule_Id(Long scheduleId);
}
