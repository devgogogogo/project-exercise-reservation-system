package com.fastcampus.exercisereservationsystem.domain.classSchedule.repository;

import com.fastcampus.exercisereservationsystem.domain.classSchedule.entity.ClassScheduleEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClassScheduleRepository extends JpaRepository<ClassScheduleEntity, Long> {

    // @Query 없이도 OK
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ClassScheduleEntity> findById(Long classSchedulesId); // @Query 없이도 OK

    List<ClassScheduleEntity> findAllByDateOrderByStartTimeAsc(LocalDate date);

    List<ClassScheduleEntity> findAllByDate(LocalDate date);

}




/**
 * 둘다 값을 가지고 오는건 같다 여기서 차이는 순서를 보장하는것과 안하는거 차이이다
 */

/**
 * 이 메서드 이름을 findByIdWithLock 이라고 해도, findByIdForUpdate 라고 해도, 기능은 똑같아.
 */

//지정한 id의 수업 스케줄 행을 읽어오면서 “쓰기용 비관락”을 건다.
//간단히 말해, “나 예약 처리하는 동안 이 스케줄 행은 내 거. 다른 트랜잭션은 잠깐 대기!”를 DB에게 요청한다.
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("select s from ClassScheduleEntity s where s.id = :id")
//    Optional<ClassScheduleEntity> findByIdForUpdate(Long id);

