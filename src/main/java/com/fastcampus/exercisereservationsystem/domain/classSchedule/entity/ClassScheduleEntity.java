package com.fastcampus.exercisereservationsystem.domain.classSchedule.entity;

import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Table(name = "schedules")
@Entity
public class ClassScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user; //개설하는건 ADMIN 관리자가 개설하는거임

    @Column(nullable = false, columnDefinition = "")
    private String className;

    private Long count;

    private Long maxCount;

    @Column(nullable = false)
    private LocalDate startAt;

    @Column(nullable = false)
    private LocalDate endAt;

    public ClassScheduleEntity(UserEntity user, String className, Long count, Long maxCount, LocalDate startAt, LocalDate endAt) {
        this.user = user;
        this.className = className;
        this.count = 0L;
        this.maxCount = maxCount;
        this.startAt = startAt;
        this.endAt = endAt;
    }
}
