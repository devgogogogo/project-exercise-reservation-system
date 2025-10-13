package com.fastcampus.exercisereservationsystem.domain.reservation.service;

import com.fastcampus.exercisereservationsystem.domain.classSchedule.entity.ClassScheduleEntity;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.repository.ClassScheduleRepository;
import com.fastcampus.exercisereservationsystem.domain.reservation.dto.response.GetReservationListResponse;
import com.fastcampus.exercisereservationsystem.domain.reservation.dto.response.ReservationResponse;
import com.fastcampus.exercisereservationsystem.domain.reservation.repository.ReservationRepository;
import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import com.fastcampus.exercisereservationsystem.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Transactional
@Testcontainers
@SpringBootTest
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClassScheduleRepository classScheduleRepository;

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.33")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:6.2")
            .withExposedPorts(6379)
            .waitingFor(org.testcontainers.containers.wait.strategy.Wait.forListeningPort());

    @DynamicPropertySource
    static void dbProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");

        //
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @DisplayName("[예약] - 수업 예약")
    @Test
    void reservation() {
        //Given
        UserEntity userEntity1 = new UserEntity("유저1", "닉네임1", "user1@email.com", "1234", LocalDate.parse("2025-10-01"), LocalDate.parse("2025-10-30"));
        UserEntity userEntity2 = new UserEntity("유저2", "닉네임2", "user2@email.com", "1234", LocalDate.parse("2025-10-01"), LocalDate.parse("2025-10-30"));
        userRepository.save(userEntity1);
        userRepository.save(userEntity2);
        ClassScheduleEntity classScheduleEntity = new ClassScheduleEntity("classname1", LocalTime.parse("10:30"), LocalTime.parse("11:30"), LocalDate.parse("2025-10-01"), 10, userEntity1);
        ClassScheduleEntity savedClassSchedule = classScheduleRepository.save(classScheduleEntity);
        //When
        ReservationResponse response1 = reservationService.reservation(savedClassSchedule.getId(), userEntity1);
        ReservationResponse response2 = reservationService.reservation(savedClassSchedule.getId(), userEntity2);//2번째 유저가 예약을함 --> 총 2명을 예약한것을 확인하기 위해
        // Then
        assertThat(response1).isNotNull();
        assertThat(response1.scheduleId()).isEqualTo(classScheduleEntity.getId());
        assertThat(response2.scheduleId()).isEqualTo(classScheduleEntity.getId());
        assertThat(response1.className()).isEqualTo("classname1");
        assertThat(response2.className()).isEqualTo("classname1");
        assertThat(response1.reservedCount()).isEqualTo(1);
        assertThat(response2.reservedCount()).isEqualTo(2);
    }
    @DisplayName("[예약] - 예약한 사람들 조회")
    @Test
    void getReservationList() {
        //Given
        UserEntity userEntity1 = new UserEntity("유저1", "닉네임1", "user1@email.com", "1234", LocalDate.parse("2025-10-01"), LocalDate.parse("2025-10-30"));
        UserEntity userEntity2 = new UserEntity("유저2", "닉네임2", "user2@email.com", "1234", LocalDate.parse("2025-10-01"), LocalDate.parse("2025-10-30"));
        userRepository.save(userEntity1);
        userRepository.save(userEntity2);
        ClassScheduleEntity classScheduleEntity = new ClassScheduleEntity("classname1", LocalTime.parse("10:30"), LocalTime.parse("11:30"), LocalDate.parse("2025-10-01"), 10, userEntity1);
        ClassScheduleEntity savedClassSchedule = classScheduleRepository.save(classScheduleEntity);
        reservationService.reservation(savedClassSchedule.getId(), userEntity1); //1번째 유저 예약
        reservationService.reservation(savedClassSchedule.getId(), userEntity2); //2번째 유저가 예약

        //When
        List<GetReservationListResponse> responses = reservationService.getReservationList(classScheduleEntity.getId());

        //then
        assertThat(responses).isNotNull();
        assertThat(responses.size()).isEqualTo(2);
        assertThat(responses.get(0).name()).isEqualTo("유저1");
        assertThat(responses.get(1).name()).isEqualTo("유저2");
    }
    @DisplayName("[예약] - 예약 취소")
    @Test
    void cancelReservation() {
        //Given
        UserEntity userEntity1 = new UserEntity("유저1", "닉네임1", "user1@email.com", "1234", LocalDate.parse("2025-10-01"), LocalDate.parse("2025-10-30"));
        UserEntity userEntity2 = new UserEntity("유저2", "닉네임2", "user2@email.com", "1234", LocalDate.parse("2025-10-01"), LocalDate.parse("2025-10-30"));
        userRepository.save(userEntity1);
        userRepository.save(userEntity2);
        ClassScheduleEntity classScheduleEntity = new ClassScheduleEntity("classname1", LocalTime.parse("10:30"), LocalTime.parse("11:30"), LocalDate.parse("2025-10-01"), 10, userEntity1);
        ClassScheduleEntity savedClassSchedule = classScheduleRepository.save(classScheduleEntity);
        ReservationResponse response1 = reservationService.reservation(savedClassSchedule.getId(), userEntity1);
        ReservationResponse response2 = reservationService.reservation(savedClassSchedule.getId(), userEntity2);

        //When
        ReservationResponse cancelResponse = reservationService.cancelReservation(classScheduleEntity.getId(), userEntity1);
        //Then
        // 반환 객체는 null이 아님 (취소 성공 시점의 상태를 리턴)
        assertThat(cancelResponse).isNotNull();
        assertThat(cancelResponse.reservedCount()).isEqualTo(1);

        //대신 DB에서 삭제 됬는지 확인하는방법이 이거다
        boolean exists = reservationRepository.findById(response1.reservationId()).isPresent();
        assertThat(exists).isFalse();

        // (3) 여전히 user2의 예약은 남아 있어야 함
        Long count = reservationRepository.countByClassSchedule_Id(savedClassSchedule.getId());
        assertThat(count).isEqualTo(1L);
    }
}