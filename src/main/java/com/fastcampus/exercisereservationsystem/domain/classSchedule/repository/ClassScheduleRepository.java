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


    List<ClassScheduleEntity> findAllByDateOrderByStartTimeAsc(LocalDate date);

    List<ClassScheduleEntity> findAllByDate(LocalDate date);

    /**
     * 둘다 값을 가지고 오는건 같다 여기서 차이는 순서를 보장하는것과 안하는거 차이이다
     */

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from ClassScheduleEntity s where s.id = :id")
    Optional<ClassScheduleEntity> findByIdForUpdate(Long id);

}


/**
 * 락(lock) 이란?
 * 데이터베이스에서 동시에 여러 사용자가 같은 데이터를 건드리지 못하게 막는 안전장치야.
 * 데이터베이스도 마찬가지로, 한 사용자가 특정 행(row) 을 수정하는 동안
 * 다른 사용자가 동시에 수정해서 데이터가 꼬이지 않도록 막아준다.
 */

/**
 *(1) 비관적 락 (Pessimistic Lock)
 * 이름 그대로 “나는 비관적이야, 다른 사람이 동시에 건드릴 거라고 가정한다”는 전략.
 * 그래서 데이터를 읽을 때부터 미리 잠가버려서 다른 트랜잭션이 접근하지 못하게 만든다.
 * DB에서는 주로 SELECT ... FOR UPDATE 라는 쿼리로 구현된다.
 * 효과: 안전하다. 동시에 여러 명이 같은 데이터를 바꾸려 할 때도 꼬이지 않는다.
 * 단점: 락을 오래 잡으면 다른 트랜잭션이 줄줄이 기다려야 해서 성능이 떨어진다.
 */

/**
 * (2) 낙관적 락 (Optimistic Lock)
 * “나는 낙관적이야, 동시에 건드릴 일이 잘 없을 거라고 가정해”라는 전략.
 * 데이터를 읽을 때는 자유롭게 읽는다. 대신 저장할 때 “버전 번호(version)” 같은 걸 비교해서, 누군가 이미 바꿔버렸으면 충돌 예외를 발생시킨다.
 * 효과: 충돌이 드문 경우 성능이 좋다.
 * 단점: 충돌이 자주 일어나면 예외가 많이 터져서 처리하기 번거롭다.
 */

/**
 * 3. 비관적 쓰기 락 (PESSIMISTIC_WRITE)
 * 비관적 락의 한 종류.
 * 특정 행을 쓰기 전용으로 잠그는 락이야.
 * “내가 지금 이 데이터 수정하려고 들어왔으니, 다른 애들은 읽기는 해도 수정은 하지 마” 라는 뜻.
 * DB는 이 행에 WRITE LOCK 을 걸어서, 다른 트랜잭션이 같은 행을 업데이트하려고 하면 대기 상태에 들어가게 된다.
 */

/**
 * 4. 예약 시스템에서 왜 비관적 락을 쓰는가?
 * 예를 들어 수업 정원이 6명인데, 동시에 두 명이 예약 버튼을 누른 상황을 생각해보자.
 * 락이 없으면:
 * 두 트랜잭션이 동시에 “현재 인원: 5명” 이라고 확인함.
 * 둘 다 “한 명 자리 남았네!” 하고 예약을 진행 → 결과적으로 7명 등록됨 → 데이터 깨짐.
 * 비관적 쓰기 락을 쓰면:
 * 첫 번째 사람이 스케줄 행을 잡으면서 락을 걸음.
 * 두 번째 사람은 대기 상태.
 * 첫 번째 사람이 예약 → 6명 됨 → 커밋 후 락 해제.
 * 두 번째 사람이 이제 실행 → 다시 확인하니 “현재 인원: 6명 → 정원 초과” → 예약 실패 처리.
 * 결과적으로 정원은 절대 안 깨짐.
 */