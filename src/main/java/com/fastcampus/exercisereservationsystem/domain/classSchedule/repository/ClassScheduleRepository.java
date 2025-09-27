package com.fastcampus.exercisereservationsystem.domain.classSchedule.repository;

import com.fastcampus.exercisereservationsystem.domain.classSchedule.entity.ClassScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClassScheduleRepository extends JpaRepository<ClassScheduleEntity, Long> {



    List<ClassScheduleEntity> findAllByDateOrderByStartTimeAsc(LocalDate date);

    List<ClassScheduleEntity> findAllByDate(LocalDate date);

    /**
     * 둘다 값을 가지고 오는건 같다 여기서 차이는 순서를 보장하는것과 안하는거 차이이다
     */


}
