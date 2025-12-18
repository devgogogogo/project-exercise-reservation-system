package com.fastcampus.exercisereservationsystem.domain.classSchedule.service;

import com.fastcampus.exercisereservationsystem.domain.classSchedule.entity.ClassScheduleEntity;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.repository.ClassScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
@Profile("test")
@RequiredArgsConstructor
public class TestDataRunner implements ApplicationRunner {

    private final ClassScheduleRepository classScheduleRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        // 이미 데이터가 있으면 다시 생성하지 않음
        if (classScheduleRepository.count() > 0) {
            return;
        }

        classScheduleRepository.save(
                new ClassScheduleEntity(
                        "Test Class A",
                        "k6 load test class A",
                        LocalTime.of(10, 0),
                        LocalTime.of(11, 0),
                        LocalDate.now(),
                        20,
                        null
                )
        );

        classScheduleRepository.save(
                new ClassScheduleEntity(
                        "Test Class B",
                        "k6 load test class B",
                        LocalTime.of(11, 0),
                        LocalTime.of(12, 0),
                        LocalDate.now(),
                        20,
                        null
                )
        );

        classScheduleRepository.save(
                new ClassScheduleEntity(
                        "Test Class C",
                        "k6 load test class C",
                        LocalTime.of(12, 0),
                        LocalTime.of(13, 0),
                        LocalDate.now(),
                        20,
                        null
                )
        );
    }
}

