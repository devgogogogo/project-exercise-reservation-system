package com.fastcampus.exercisereservationsystem.domain.classSchedule.service;

import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.request.CreateClassScheduleRequest;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.request.UpdateClassScheduleRequest;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.response.CreateClassScheduleResponse;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.response.GetClassScheduleResponse;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.response.UpdateClassScheduleResponse;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.entity.ClassScheduleEntity;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.repository.ClassScheduleRepository;
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

@Transactional
@Testcontainers
@ActiveProfiles("test")
@SpringBootTest
class ClassScheduleServiceTest {

    @Autowired
    private ClassScheduleService classScheduleService;

    @Autowired
    private ClassScheduleRepository classScheduleRepository;

    @Autowired
    private UserRepository userRepository;  //FK 충족을 위해 필요

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

    @DisplayName("[수업스케쥴] - 수업 생성")
    @Test
    void createClassSchedule() {
        //Given
        UserEntity userEntity = new UserEntity("이귀현", "닉네임", "test@email.com", "1234", LocalDate.now(), LocalDate.now().plusYears(1));
        userRepository.save(userEntity);

        CreateClassScheduleRequest request = new CreateClassScheduleRequest("classname1", "수업내용1", LocalTime.parse("10:30"), LocalTime.parse("11:30"), LocalDate.parse("2025-10-01"), 12);
        //When
        CreateClassScheduleResponse response = classScheduleService.createClassSchedule(request, userEntity);

        //Then
        assertThat(response).isNotNull();
        assertThat(response.classname()).isEqualTo("classname1");
        assertThat(response.startTime()).isEqualTo(LocalTime.parse("10:30"));
        assertThat(response.endTime()).isEqualTo(LocalTime.parse("11:30"));
        assertThat(response.date()).isEqualTo(LocalDate.parse("2025-10-01"));
        assertThat(response.capacity()).isEqualTo(12);
    }

    @DisplayName("[수업 스케쥴] - 수업 조회")
    @Test
    void getByDate() {
        //Given
        UserEntity userEntity = new UserEntity("이귀현", "닉네임", "test@email.com", "1234", LocalDate.now(), LocalDate.now().plusYears(1));
        userRepository.save(userEntity);
        ClassScheduleEntity classScheduleEntity1 = new ClassScheduleEntity("classname1", "수업내용1", LocalTime.parse("10:30"), LocalTime.parse("11:30"), LocalDate.parse("2025-10-01"), 10, userEntity);
        ClassScheduleEntity classScheduleEntity2 = new ClassScheduleEntity("classname2", "수업내용2", LocalTime.parse("11:40"), LocalTime.parse("12:40"), LocalDate.parse("2025-10-01"), 10, userEntity);
        //하나는 다른 날짜 수업 생성
        ClassScheduleEntity classScheduleEntity3 = new ClassScheduleEntity("classname3", "수업내용3", LocalTime.parse("11:50"), LocalTime.parse("12:50"), LocalDate.parse("2025-10-02"), 10, userEntity);
        classScheduleRepository.save(classScheduleEntity1);
        classScheduleRepository.save(classScheduleEntity2);
        classScheduleRepository.save(classScheduleEntity3);

        //When
        List<GetClassScheduleResponse> result = classScheduleService.getByDate(LocalDate.parse("2025-10-01"));

        //Then
        assertThat(result).hasSize(2); //그래서 2개
        assertThat(result.get(0).classname()).isEqualTo("classname1");
        assertThat(result.get(0).startAt()).isEqualTo(LocalTime.parse("10:30"));
        assertThat(result.get(0).endAt()).isEqualTo(LocalTime.parse("11:30"));
        assertThat(result.get(0).date()).isEqualTo(LocalDate.parse("2025-10-01"));
        assertThat(result.get(0).capacity()).isEqualTo(10);

        assertThat(result).hasSize(2); //그래서 2개
        assertThat(result.get(1).classname()).isEqualTo("classname2");
        assertThat(result.get(1).startAt()).isEqualTo(LocalTime.parse("11:40"));
        assertThat(result.get(1).endAt()).isEqualTo(LocalTime.parse("12:40"));
        assertThat(result.get(1).date()).isEqualTo(LocalDate.parse("2025-10-01"));
        assertThat(result.get(1).capacity()).isEqualTo(10);

    }

    @DisplayName("[수업 스케쥴] - 수업 수정")
    @Test
    void updateClassSchedule() {
        //Given
        UserEntity userEntity = new UserEntity("이귀현", "닉네임", "test@email.com", "1234", LocalDate.now(), LocalDate.now().plusYears(1));
        userRepository.save(userEntity);
        ClassScheduleEntity classScheduleEntity = new ClassScheduleEntity("classname1", "수업내용1", LocalTime.parse("10:30"), LocalTime.parse("11:30"), LocalDate.parse("2025-10-01"), 10, userEntity);
        ClassScheduleEntity save = classScheduleRepository.save(classScheduleEntity);

        UpdateClassScheduleRequest request = new UpdateClassScheduleRequest("classname2", "수업내용2", LocalTime.parse("12:30"), LocalTime.parse("13:30"), LocalDate.parse("2025-10-30"), 12);
        Long scheduleId = save.getId();

        //When
        UpdateClassScheduleResponse response = classScheduleService.updateClassSchedule(request, scheduleId);
        //Then
        assertThat(response).isNotNull();
        assertThat(response.classname()).isEqualTo("classname2");
        assertThat(response.startTime()).isEqualTo(LocalTime.parse("12:30"));
        assertThat(response.endTime()).isEqualTo(LocalTime.parse("13:30"));
        assertThat(response.date()).isEqualTo(LocalDate.parse("2025-10-30"));
        assertThat(response.capacity()).isEqualTo(12);
    }

    @DisplayName("[수업 스케쥴] - 수업 삭제")
    @Test
    void deleteClassSchedule() {
        //Given
        UserEntity userEntity = new UserEntity("이귀현", "닉네임", "test@email.com", "1234", LocalDate.now(), LocalDate.now().plusYears(1));
        userRepository.save(userEntity);
        ClassScheduleEntity classScheduleEntity = new ClassScheduleEntity("classname1", "수업내용1", LocalTime.parse("10:30"), LocalTime.parse("11:30"), LocalDate.parse("2025-10-01"), 10, userEntity);
        ClassScheduleEntity response = classScheduleRepository.save(classScheduleEntity);
        //When
        classScheduleService.deleteClassSchedule(response.getId());
        boolean exists = classScheduleRepository.findById(response.getId()).isPresent();

        //Then
        assertThat(response).isNotNull();
        assertThat(exists).isFalse();

    }
}