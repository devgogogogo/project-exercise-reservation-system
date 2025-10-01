package com.fastcampus.exercisereservationsystem.domain.reservation.entity;

import com.fastcampus.exercisereservationsystem.common.BaseEntity;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.entity.ClassScheduleEntity;
import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.time.LocalDateTime;

@Table(name = "reservations")
@NoArgsConstructor
@Getter
@Entity
public class ReservationEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * "예약에는 반드시 유저가 있어야 한다"
     * 즉, 예약 하나가 User 없이 존재하는 건 말이 안 됨.
     * 따라서 여기서는 @ManyToOne(optional = false) 가 맞아.
     * (예약이 생기려면 반드시 누군가가 예약을 해야 하니까)
     */
    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    private UserEntity user;


    /**
     * 2) Reservation → ClassSchedule 관계
     * "예약에는 반드시 수업(ClassSchedule)이 있어야 한다"
     * 수업이 없는데 예약이 있을 수는 없음.
     * 따라서 여기서도 optional = false 가 맞아.
     */
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "class_schedule_id", nullable = false)
    private ClassScheduleEntity classSchedule;

    public ReservationEntity( UserEntity user, ClassScheduleEntity classSchedule) {
        this.user = user;
        this.classSchedule = classSchedule;
    }
}


/**
 * ) ClassSchedule → Reservation 관계 (반대 방향)
 * "수업은 존재하지만, 예약이 하나도 없을 수 있다"
 * 맞아, 수업은 열렸지만 아무도 신청하지 않을 수 있지.
 * 하지만 이건 ClassSchedule 입장에서 예약이 없을 수도 있다는 뜻이지,
 * Reservation 입장에서 ClassSchedule 이 없어도 된다는 뜻은 아니야.
 */