package com.fastcampus.exercisereservationsystem.domain.reservation.repository;

import com.fastcampus.exercisereservationsystem.domain.reservation.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity,Long> {

    //예약을 했는지 중복체크하기 위해
    boolean existsByClassSchedule_IdAndUser_Id(Long scheduleId, Long userId);

    //  해당 스케줄에 이 유저가 만든 예약 한 건 찾기
    Optional<ReservationEntity> findByClassSchedule_IdAndUser_Id(Long scheduleId, Long userId);

    //예약한 인원
    Long countByClassSchedule_Id(Long scheduleId);

    //먼저 예약한 순서대로 정렬. (원하지 않으면 빼도 됨.)
    List<ReservationEntity> findByClassSchedule_IdOrderByCreatedAtAsc(Long classScheduleId);
}
