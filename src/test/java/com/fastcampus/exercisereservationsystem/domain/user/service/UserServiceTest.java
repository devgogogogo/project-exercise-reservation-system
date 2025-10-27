package com.fastcampus.exercisereservationsystem.domain.user.service;

import com.fastcampus.exercisereservationsystem.common.service.JwtService;
import com.fastcampus.exercisereservationsystem.domain.user.dto.request.CreateUserRequest;
import com.fastcampus.exercisereservationsystem.domain.user.dto.request.LoginUserRequest;
import com.fastcampus.exercisereservationsystem.domain.user.dto.request.UpdateUserRequest;
import com.fastcampus.exercisereservationsystem.domain.user.dto.response.CreateUserResponse;
import com.fastcampus.exercisereservationsystem.domain.user.dto.response.GetUserResponse;
import com.fastcampus.exercisereservationsystem.domain.user.dto.response.LoginUserResponse;
import com.fastcampus.exercisereservationsystem.domain.user.dto.response.UpdateUserResponse;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Transactional
@Testcontainers
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

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
    @Autowired
    private JwtService jwtService;

    @DynamicPropertySource
    static void dbProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");

        //
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
        // JWT 시크릿(HS256용 최소 32바이트, Base64 인코딩 문자열)
        String raw = "Y3Jhc2gtc2VydmljZS1zdXBlci1zZXZyZXQta2V5203874yosdhgf";
        String b64 = java.util.Base64.getEncoder().encodeToString(raw.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        registry.add("jwt.secret-key", () -> b64);
    }


    @DisplayName("[유저] - 회원가입")
    @Test
    void signup() {
        //Given
        CreateUserRequest request = new CreateUserRequest("이귀현", "닉네임", "test@email.com", "1234", LocalDate.parse("2025-10-01"), LocalDate.parse("2025-10-30"));

        //When
        CreateUserResponse response = userService.signup(request);

        //Then
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("이귀현");
        assertThat(response.nickname()).isEqualTo("닉네임");
        assertThat(response.username()).isEqualTo("test@email.com");
        assertThat(response.startedAt()).isEqualTo("2025-10-01");  //형식 문자열을 명확히 맞춘다면 가능
        assertThat(response.endedAt()).isEqualTo(LocalDate.parse("2025-10-30"));
    }

    @DisplayName("[유저] - 로그인")
    @Test
    void login() {
        //Given
        String username = "test@email.com";
        String plainPw = "password123"; // 8자 이상
        CreateUserRequest request = new CreateUserRequest("이귀현", "닉네임", username, plainPw, LocalDate.parse("2025-10-01"), LocalDate.parse("2025-10-30"));
        userService.signup(request);

        //When
        LoginUserRequest loginReq = new LoginUserRequest(username, plainPw);
        LoginUserResponse response = userService.login(loginReq);
        //Then
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isNotBlank();

        String subject = jwtService.extractUsername(response.accessToken());
        assertThat(subject).isEqualTo(username);
    }

    @DisplayName("[유저] 로그인 실패")
    @Test
    void login_fail() {
        //Given
        String username = "me@site.com";
        String pw = "strongpass";
        userService.signup(new CreateUserRequest("홍길동", "길동", username, pw, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31)
        ));
        //When & Then
        LoginUserRequest wrong = new LoginUserRequest(username, "wrongpass");
        assertThatThrownBy(() -> userService.login(wrong))
                .isInstanceOf(org.springframework.web.server.ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(org.springframework.http.HttpStatus.UNAUTHORIZED);
    }

    @DisplayName("[유저] 회원 전체 조회")
    @Test
    void getUserList() {
        //Given
        userRepository.save(new UserEntity("유저1", "닉네임1", "user1@email.com", "1234", LocalDate.parse("2025-10-01"), LocalDate.parse("2025-10-30")));
        userRepository.save(new UserEntity("유저2", "닉네임2", "user2@email.com", "1234", LocalDate.parse("2025-10-01"), LocalDate.parse("2025-10-30")));
        userRepository.save(new UserEntity("유저3", "닉네임3", "user3@email.com", "1234", LocalDate.parse("2025-10-01"), LocalDate.parse("2025-10-30")));
        //When
        List<GetUserResponse> responses = userService.getUserList();
        //Then
        assertThat(responses).hasSize(5);  //DevDataConfig 클래스에 2명 만들어놔서 5명임 (관리자1명, 일반유저 1명)
        assertThat(responses.stream().map(GetUserResponse::username).toList())
                .contains("user1@email.com", "user2@email.com", "user3@email.com");
        assertThat(responses.stream().map(getUserResponse -> getUserResponse.name()).toList())
                .contains("유저1", "유저2", "유저3");
    }

    @DisplayName("[유저] 회원 단건 조회")
    @Test
    void getUserById() {
        //Given
        UserEntity saved = userRepository.save(new UserEntity("유저1", "닉네임1", "user1@email.com", "1234", LocalDate.parse("2025-10-01"), LocalDate.parse("2025-10-30")));
        //When
        GetUserResponse response = userService.getUserById(saved.getId());
        //Then
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("유저1");
        assertThat(response.nickname()).isEqualTo("닉네임1");
        assertThat(response.username()).isEqualTo("user1@email.com");
        assertThat(response.startedAt()).isEqualTo("2025-10-01");
        assertThat(response.endedAt()).isEqualTo(LocalDate.parse("2025-10-30"));
    }

    @DisplayName("[유저] 회원 수정")
    @Test
    void updateUserPeriod() {
        //Given
        UserEntity saved = userRepository.save(new UserEntity("유저1", "닉네임1", "user1@email.com", "1234", LocalDate.parse("2025-10-01"), LocalDate.parse("2025-10-30")));
        UpdateUserRequest request = new UpdateUserRequest(LocalDate.parse("2025-11-01"), LocalDate.parse("2025-11-30"));
        //When
        UpdateUserResponse response = userService.updateUserPeriod(saved.getId(), request);
        //Then
        assertThat(response).isNotNull();
        assertThat(response.username()).isEqualTo("user1@email.com");
        assertThat(response.startedAt()).isEqualTo("2025-11-01");
        assertThat(response.endedAt()).isEqualTo(LocalDate.parse("2025-11-30"));
    }

    @DisplayName("[유저] 회원 삭제")
    @Test
    void deleteUser() {
        //Given
        UserEntity saved = userRepository.save(new UserEntity("유저1", "닉네임1", "user1@email.com", "1234", LocalDate.parse("2025-10-01"), LocalDate.parse("2025-10-30")));
        //When
        userService.deleteUser(saved.getUsername());
        Optional<UserEntity> response = userRepository.findById(saved.getId());
        boolean exists = userRepository.findById(saved.getId()).isPresent();
        //Then
        assertThat(response).isNotNull();
        assertThat(exists).isFalse();
    }
}