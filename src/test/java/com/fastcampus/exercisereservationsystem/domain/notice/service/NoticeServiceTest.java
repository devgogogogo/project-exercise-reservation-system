package com.fastcampus.exercisereservationsystem.domain.notice.service;

import com.fastcampus.exercisereservationsystem.domain.notice.dto.request.CreateNoticeRequest;
import com.fastcampus.exercisereservationsystem.domain.notice.dto.request.UpdateNoticeRequest;
import com.fastcampus.exercisereservationsystem.domain.notice.dto.response.CreateNoticeResponse;
import com.fastcampus.exercisereservationsystem.domain.notice.dto.response.GetNoticeListResponse;
import com.fastcampus.exercisereservationsystem.domain.notice.dto.response.GetNoticeResponse;
import com.fastcampus.exercisereservationsystem.domain.notice.dto.response.UpdateNoticeResponse;
import com.fastcampus.exercisereservationsystem.domain.notice.entity.NoticeEntity;
import com.fastcampus.exercisereservationsystem.domain.notice.repository.NoticeRepository;
import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import com.fastcampus.exercisereservationsystem.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Transactional
@Testcontainers
@SpringBootTest
class NoticeServiceTest {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private NoticeRepository noticeRepository;

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

    @DisplayName("[공지사항] 공지사항 생성")
    @Test
    void createNotice() {
        //Given
        UserEntity userEntity = new UserEntity("이귀현", "닉네임", "test@email.com", "1234", LocalDate.now(), LocalDate.now().plusYears(1));
        userRepository.save(userEntity);
        CreateNoticeRequest request = new CreateNoticeRequest("공지사항 제목", "공지사항 내용");

        //When
        CreateNoticeResponse response = noticeService.createNotice(request, userEntity);

        //Then
        assertThat(response).isNotNull();
        assertThat(response.title()).isEqualTo("공지사항 제목");
        assertThat(response.description()).isEqualTo("공지사항 내용");
    }

    @DisplayName("[공지사항] - 페이지 조회")
    @Test
    void getNoticeList() {
        //Given
        UserEntity userEntity = new UserEntity("이귀현", "닉네임", "test@email.com", "1234", LocalDate.now(), LocalDate.now().plusYears(1));
        userRepository.save(userEntity);
        noticeRepository.save(new NoticeEntity("공지사항 제목1", "공지사항 내용1", userEntity));
        noticeRepository.save(new NoticeEntity("공지사항 제목2", "공지사항 내용2", userEntity));
        noticeRepository.save(new NoticeEntity("공지사항 제목3", "공지사항 내용3", userEntity));
        noticeRepository.save(new NoticeEntity("공지사항 제목4", "공지사항 내용4", userEntity));
        noticeRepository.save(new NoticeEntity("공지사항 제목5", "공지사항 내용5", userEntity));

        //When
        Page<GetNoticeListResponse> responses = noticeService.getNoticeList(1, 3);

        //Then
        assertThat(responses).isNotNull();
        assertThat(responses.getTotalElements()).isEqualTo(5);
        assertThat(responses.getTotalPages()).isEqualTo(2);
        assertThat(responses.getContent()).hasSize(3);
        assertThat(responses.getNumber()).isEqualTo(0);
    }

    @DisplayName("[공지사항] - 단건조회")
    @Test
    void getNotice() {
        //Given
        UserEntity userEntity = new UserEntity("이귀현", "닉네임", "test@email.com", "1234", LocalDate.now(), LocalDate.now().plusYears(1));
        userRepository.save(userEntity);
        NoticeEntity noticeEntity = new NoticeEntity("공지사항 제목", "공지사항 내용", userEntity);
        NoticeEntity saved = noticeRepository.save(noticeEntity);

        //When
        GetNoticeResponse response = noticeService.getNotice(saved.getId());

        //Then
        assertThat(response).isNotNull();
        assertThat(response.title()).isEqualTo("공지사항 제목");
        assertThat(response.description()).isEqualTo("공지사항 내용");
    }


    @DisplayName("[공지사항] - 검색")
    @Test
    void searchByKeywordJpql() {
        //Given
        UserEntity userEntity = new UserEntity("이귀현", "닉네임", "test@email.com", "1234", LocalDate.now(), LocalDate.now().plusYears(1));
        userRepository.save(userEntity);
        NoticeEntity n1 = noticeRepository.save(new NoticeEntity("운영 안내", "시스템 점검 예정", userEntity));
        NoticeEntity n2 = noticeRepository.save(new NoticeEntity("점검 공지", "이번 주말 점검 진행", userEntity));  // "공지", "점검" 포함
        NoticeEntity n3 = noticeRepository.save(new NoticeEntity("신규 프로그램 런칭", "신규 강좌 공지 사항 안내", userEntity)); // "공지" 포함
        NoticeEntity n4 = noticeRepository.save(new NoticeEntity("주말 휴무", "휴무 안내 드립니다", userEntity));
        NoticeEntity n5 = noticeRepository.save(new NoticeEntity("이벤트 공지", "가을 이벤트 진행", userEntity)); // "공지" 포함
        //When
        Page<GetNoticeResponse> page1 = noticeService.searchByKeywordJpql("공지", 1, 3);
        //Then
        assertThat(page1).isNotNull();
        assertThat(page1.getNumber()).isEqualTo(0);       // safePage = 1 - 1 = 0, 0번 페이지
        assertThat(page1.getSize()).isEqualTo(3);         // 요청한 페이지 크기(size)
        assertThat(page1.getContent()).hasSize(3);

        assertThat(page1.getContent().get(0).title()).isEqualTo("이벤트 공지");         // n5
        assertThat(page1.getContent().get(1).title()).isEqualTo("신규 프로그램 런칭"); // n3
        assertThat(page1.getContent().get(2).title()).isEqualTo("점검 공지");           // n2

    }

    @DisplayName("[공지사항] - 수정")
    @Test
    void updateNotice() {
        //Given
        UserEntity userEntity = new UserEntity("이귀현", "닉네임", "test@email.com", "1234", LocalDate.now(), LocalDate.now().plusYears(1));
        userRepository.save(userEntity);
        NoticeEntity noticeEntity = new NoticeEntity("공지사항 제목", "공지사항 내용", userEntity);
        noticeRepository.save(noticeEntity);
        UpdateNoticeRequest request = new UpdateNoticeRequest("수정한 공지사항", "수정한 공지사항 내용");

        //When
        UpdateNoticeResponse response = noticeService.updateNotice(userEntity, request, noticeEntity.getId());

        //Then
        assertThat(response).isNotNull();
        assertThat(response.title()).isEqualTo("수정한 공지사항");
        assertThat(response.description()).isEqualTo("수정한 공지사항 내용");
    }

    @DisplayName("[공지사항] - 삭제")
    @Test
    void deleteNotice() {
        //Given
        UserEntity userEntity = new UserEntity("이귀현", "닉네임", "test@email.com", "1234", LocalDate.now(), LocalDate.now().plusYears(1));
        userRepository.save(userEntity);
        NoticeEntity noticeEntity = new NoticeEntity("공지사항 제목", "공지사항 내용", userEntity);
        NoticeEntity saved = noticeRepository.save(noticeEntity);
        //When
        noticeService.deleteNotice(userEntity,saved.getId());
        Optional<NoticeEntity> response = noticeRepository.findById(saved.getId());
        boolean exists = noticeRepository.findById(saved.getId()).isPresent();

        //Then
        assertThat(response).isNotNull();
        assertThat(exists).isFalse();
    }
}