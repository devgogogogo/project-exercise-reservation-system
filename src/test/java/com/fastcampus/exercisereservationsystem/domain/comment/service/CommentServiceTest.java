package com.fastcampus.exercisereservationsystem.domain.comment.service;

import com.fastcampus.exercisereservationsystem.common.exception.BizException;
import com.fastcampus.exercisereservationsystem.domain.comment.dto.request.CreateCommentRequest;
import com.fastcampus.exercisereservationsystem.domain.comment.dto.request.UpdateCommentRequest;
import com.fastcampus.exercisereservationsystem.domain.comment.dto.response.CreateCommentResponse;
import com.fastcampus.exercisereservationsystem.domain.comment.dto.response.GetCommentListResponse;
import com.fastcampus.exercisereservationsystem.domain.comment.dto.response.UpdateCommentResponse;
import com.fastcampus.exercisereservationsystem.domain.comment.entity.CommentEntity;
import com.fastcampus.exercisereservationsystem.domain.comment.exception.CommentErrorCode;
import com.fastcampus.exercisereservationsystem.domain.comment.repository.CommentRepository;
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
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@ActiveProfiles("test")
@Transactional
@SpringBootTest
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private CommentRepository commentRepository;

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

    @DisplayName("[댓글] 댓글생성")
    @Test
    void createComment() {
        //Given
        UserEntity userEntity = new UserEntity("이귀현", "닉네임", "test@email.com", "1234", LocalDate.now(), LocalDate.now().plusYears(1));
        userRepository.save(userEntity);
        NoticeEntity noticeEntity = new NoticeEntity("공지사항 제목", "공지사항 내용", userEntity);
        NoticeEntity saved = noticeRepository.save(noticeEntity);
        CreateCommentRequest request = new CreateCommentRequest("댓글");

        //When
        CreateCommentResponse response = commentService.createComment(userEntity, saved.getId(), request);
        //Then
        assertThat(response).isNotNull();
        assertThat(response.description()).isEqualTo("댓글");
    }

    @DisplayName("[댓글] 댓글조회")
    @Test
    void getCommentPage() {
        //Given
        UserEntity userEntity = new UserEntity("이귀현", "닉네임", "test@email.com", "1234", LocalDate.now(), LocalDate.now().plusYears(1));
        userRepository.save(userEntity);
        NoticeEntity noticeEntity = new NoticeEntity("공지사항 제목", "공지사항 내용", userEntity);
        NoticeEntity saved = noticeRepository.save(noticeEntity);
        CommentEntity c1 = commentRepository.save(new CommentEntity(saved, userEntity, "첫 댓글"));
        CommentEntity c2 = commentRepository.save(new CommentEntity(saved, userEntity, "둘 댓글"));
        CommentEntity c3 = commentRepository.save(new CommentEntity(saved, userEntity, "셋 댓글"));

        //When
        Page<GetCommentListResponse> page1 = commentService.getCommentPage(saved.getId(), 1, 2);

        //Then
        assertThat(page1.getTotalElements()).isEqualTo(3); //전체 데이터 개수
        assertThat(page1.getTotalPages()).isEqualTo(2); //전체 페이지 수 (totalElements / size 계산 결과)
        assertThat(page1.getNumber()).isEqualTo(0); // 0-based //현재 페이지 번호 (0부터 시작)
        assertThat(page1.getSize()).isEqualTo(2);
        assertThat(page1.isFirst()).isTrue(); //현재 페이지가 첫페이지인지 여부
        assertThat(page1.isLast()).isFalse(); //마지막인지 여부

        assertThat(page1.getContent()).hasSize(2);  //한 페이지당 데이터 개수
        assertThat(page1.getContent().get(0).commentId()).isEqualTo(c3.getId());
        assertThat(page1.getContent().get(1).commentId()).isEqualTo(c2.getId());
    }

    @DisplayName("[댓글] 댓글수정")
    @Test
    void updateComment() {
        //Given
        UserEntity userEntity = new UserEntity("이귀현", "닉네임", "test@email.com", "1234", LocalDate.now(), LocalDate.now().plusYears(1));
        userRepository.save(userEntity);
        NoticeEntity noticeEntity = new NoticeEntity("공지사항 제목", "공지사항 내용", userEntity);
        NoticeEntity savedNoticeEntity = noticeRepository.save(noticeEntity);
        CommentEntity commentEntity = new CommentEntity(noticeEntity, userEntity, "댓글");
        CommentEntity savedCommentEntity = commentRepository.save(commentEntity);
        UpdateCommentRequest request = new UpdateCommentRequest("수정된 댓글");

        //When
        UpdateCommentResponse response = commentService.updateComment(userEntity, savedNoticeEntity.getId(), savedCommentEntity.getId(), request);
        //Then
        assertThat(response).isNotNull();
        assertThat(response.description()).isEqualTo("수정된 댓글");

    }

    @DisplayName("[댓글] 댓글삭제")
    @Test
    void deleteComment() {
        //Given
        UserEntity userEntity = new UserEntity("이귀현", "닉네임", "test@email.com", "1234", LocalDate.now(), LocalDate.now().plusYears(1));
        userRepository.save(userEntity);
        NoticeEntity noticeEntity = new NoticeEntity("공지사항 제목", "공지사항 내용", userEntity);
        noticeRepository.save(noticeEntity);
        CommentEntity commentEntity = new CommentEntity(noticeEntity, userEntity, "댓글");
        CommentEntity savedCommentEntity = commentRepository.save(commentEntity);

        //When
        commentRepository.deleteById(savedCommentEntity.getId());
        Optional<CommentEntity> response = commentRepository.findById(savedCommentEntity.getId());
        boolean exists = commentRepository.findById(savedCommentEntity.getId()).isPresent();

        //Then
        assertThat(response).isNotNull();
        assertThat(exists).isFalse();
    }
}