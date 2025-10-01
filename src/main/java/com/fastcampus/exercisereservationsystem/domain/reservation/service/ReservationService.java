package com.fastcampus.exercisereservationsystem.domain.reservation.service;

import com.fastcampus.exercisereservationsystem.common.exception.BizException;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.entity.ClassScheduleEntity;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.exception.ClassScheduleErrorCode;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.repository.ClassScheduleRepository;
import com.fastcampus.exercisereservationsystem.domain.reservation.dto.response.GetReservationListResponse;
import com.fastcampus.exercisereservationsystem.domain.reservation.dto.response.ReservationResponse;
import com.fastcampus.exercisereservationsystem.domain.reservation.entity.ReservationEntity;
import com.fastcampus.exercisereservationsystem.domain.reservation.exception.ReservationErrorCode;
import com.fastcampus.exercisereservationsystem.domain.reservation.repository.ReservationRepository;
import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ClassScheduleRepository classScheduleRepository;

    @Transactional
    public ReservationResponse reservation(Long scheduleId, UserEntity userEntity) {

        // 1) 스케줄 조회(+락) -동시성 안전하게 정원 체크
        ClassScheduleEntity classScheduleEntity = classScheduleRepository.findByIdForUpdate(scheduleId).orElseThrow(() -> new BizException(ClassScheduleErrorCode.SCHEDULE_NOT_FOUND));

        //2) 중복예약 방지
        if (reservationRepository.existsByClassSchedule_IdAndUser_Id(scheduleId, userEntity.getId())) {
           throw new BizException(ClassScheduleErrorCode.SCHEDULE_NOT_FOUND);
        }
        Long current = reservationRepository.countByClassSchedule_Id(scheduleId);
        if (current >= classScheduleEntity.getCapacity()) {
            throw new BizException(ReservationErrorCode.RESERVATION_CAPACITY_FULL);
        }

        ReservationEntity reservationEntity = new ReservationEntity(userEntity, classScheduleEntity);
        reservationRepository.save(reservationEntity);

        Long reservedCount = reservationRepository.countByClassSchedule_Id(scheduleId);

        //3) 정원 체크
        return ReservationResponse.from(reservationEntity,reservedCount);
    }


    @Transactional
    public List<GetReservationListResponse> getReservationList(Long scheduleId) {

        List<ReservationEntity> reservationEntities = reservationRepository.findByClassSchedule_IdOrderByCreatedAtAsc(scheduleId);
       return reservationEntities.stream()
                .map(entity -> GetReservationListResponse.from(entity.getUser()))
                .toList();
    }


    @Transactional
    public ReservationResponse cancelReservation(Long scheduleId, UserEntity userEntity) {
        //1) 스케쥴 잠그기 (동시 취소/재예약 경합 대비)
        classScheduleRepository.findById(scheduleId).orElseThrow(() -> new BizException(ClassScheduleErrorCode.SCHEDULE_NOT_FOUND));
        ReservationEntity reservationEntity = reservationRepository.findByClassSchedule_IdAndUser_Id(scheduleId, userEntity.getId()).orElseThrow(() -> new BizException(ReservationErrorCode.RESERVATION_NOT_FOUND));
        reservationRepository.delete(reservationEntity);
        Long reservedCount = reservationRepository.countByClassSchedule_Id(scheduleId);
        return ReservationResponse.from(reservationEntity,reservedCount);
    }
}
