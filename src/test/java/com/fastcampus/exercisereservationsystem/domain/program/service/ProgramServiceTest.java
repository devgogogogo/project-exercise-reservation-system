package com.fastcampus.exercisereservationsystem.domain.program.service;

import com.fastcampus.exercisereservationsystem.domain.program.dto.request.CreateProgramRequest;
import com.fastcampus.exercisereservationsystem.domain.program.dto.request.UpdateProgramRequest;
import com.fastcampus.exercisereservationsystem.domain.program.dto.response.CreateProgramResponse;
import com.fastcampus.exercisereservationsystem.domain.program.dto.response.GetByDateProgramResponse;
import com.fastcampus.exercisereservationsystem.domain.program.dto.response.UpdateProgramResponse;
import com.fastcampus.exercisereservationsystem.domain.program.entity.ProgramEntity;
import com.fastcampus.exercisereservationsystem.domain.program.repository.ProgramRepository;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Transactional
@Testcontainers
@SpringBootTest
class ProgramServiceTest {

    @Autowired
    private ProgramService programService;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private UserRepository userRepository;

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


    @DisplayName("[프로그램] - 생성")
    @Test
    void createProgram() {
        //Given
        UserEntity userEntity = new UserEntity("이귀현", "닉네임", "test@email.com", "1234", LocalDate.now(), LocalDate.now().plusYears(1));
        userRepository.save(userEntity);
        CreateProgramRequest request = new CreateProgramRequest("수업이름", "수업내용", LocalDate.parse("2025-10-01"));
        //When
        CreateProgramResponse response = programService.createProgram(request, userEntity);
        //Then
        assertThat(response).isNotNull();
        assertThat(response.exerciseName()).isEqualTo("수업이름");
    }

    @DisplayName("[프로그램] - 조회")
    @Test
    void getByDateProgram() {
        //Given
        UserEntity userEntity = new UserEntity("이귀현", "닉네임", "test@email.com", "1234", LocalDate.now(), LocalDate.now().plusYears(1));
        userRepository.save(userEntity);
        ProgramEntity programEntity = new ProgramEntity(userEntity, "수업이름", "수업내용", LocalDate.parse("2025-10-01"));
        ProgramEntity save = programRepository.save(programEntity);
        //When
        List<GetByDateProgramResponse> responses = programService.getByDateProgram(LocalDate.parse("2025-10-01"));
        //Then
        assertThat(responses).hasSize(1);

        GetByDateProgramResponse r = responses.get(0);

        assertThat(r.id()).isEqualTo(save.getId());
        assertThat(r.exerciseName()).isEqualTo("수업이름");
        assertThat(r.exerciseDescription()).isEqualTo("수업내용");

    }

    @DisplayName("[프로그램] - 수정")
    @Test
    void updateProgram() {
        //Given
        UserEntity userEntity = new UserEntity("이귀현", "닉네임", "test@email.com", "1234", LocalDate.now(), LocalDate.now().plusYears(1));
        userRepository.save(userEntity);
        ProgramEntity programEntity = new ProgramEntity(userEntity, "수업이름", "수업내용", LocalDate.parse("2025-10-01"));
        ProgramEntity save = programRepository.save(programEntity);
        UpdateProgramRequest request = new UpdateProgramRequest("수정된 수업이름", "수정된 수업내용", LocalDate.parse("2025-10-02"));
        //When
        UpdateProgramResponse response = programService.updateProgram(save.getId(), request);
        //Then
        assertThat(response).isNotNull();
        assertThat(response.exerciseName()).isEqualTo("수정된 수업이름");
        assertThat(response.exerciseDescription()).isEqualTo("수정된 수업내용");
        assertThat(response.date()).isEqualTo("2025-10-02");
    }

    @DisplayName("[프로그램] - 삭제")
    @Test
    void deleteProgram() {
        //Given
        UserEntity userEntity = new UserEntity("이귀현", "닉네임", "test@email.com", "1234", LocalDate.now(), LocalDate.now().plusYears(1));
        userRepository.save(userEntity);
        ProgramEntity programEntity = new ProgramEntity(userEntity, "수업이름", "수업내용", LocalDate.parse("2025-10-01"));
        ProgramEntity save = programRepository.save(programEntity);
        //When
        programService.deleteProgram(save.getId());
        Optional<ProgramEntity> response = programRepository.findById(save.getId());
        boolean exists = programRepository.findById(save.getId()).isPresent();
        //Then
        assertThat(exists).isFalse();
        assertThat(response).isEmpty();
    }
}