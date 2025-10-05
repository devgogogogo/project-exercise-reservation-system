package com.fastcampus.exercisereservationsystem.domain.classSchedule.entity;

import com.fastcampus.exercisereservationsystem.domain.classSchedule.enums.ScheduleStatus;
import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@Table(name = "schedules",indexes = {
//        @Index(name = "inx_테이블명_컬럼명",columnList = "칼럼이름여긴 일치 시켜야한다."),
        @Index(name = "ind_schedule_date",columnList = "date")
})
@Entity
public class ClassScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String className;

    @Column(nullable = false)
    private LocalTime startTime; //시간시간

    @Column(nullable = false)
    private LocalTime endTime; //종료시간

    @Column(nullable = false)
    private LocalDate date; // 그날

    @Column(nullable = false)
    private Integer capacity; //예약가능한 인원

    @Enumerated(EnumType.STRING)
    private ScheduleStatus scheduleStatus;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user; //개설하는건 ADMIN 관리자가 개설하는거임

    public ClassScheduleEntity(String className, LocalTime startTime, LocalTime endTime, LocalDate date, Integer capacity, UserEntity user) {
        this.className = className;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.capacity = capacity;
        this.scheduleStatus = ScheduleStatus.OPEN;
        this.user = user;
    }

    public void updateClassSchedule(String className, LocalTime startTime, LocalTime endTime, LocalDate date, Integer capacity) {
        this.className = className;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.capacity = capacity;
    }
}
