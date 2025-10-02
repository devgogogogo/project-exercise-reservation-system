package com.fastcampus.exercisereservationsystem.domain.comment.repository;

import com.fastcampus.exercisereservationsystem.domain.comment.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {


    @Query("select c from CommentEntity c join fetch c.user where c.notice.id = :noticeId")
    List<CommentEntity> findByNoticeIdWithUser(@Param("noticeId") Long noticeId);

    @Query("select c from CommentEntity c join fetch c.user where c.id = :commentId")
    Optional<CommentEntity> findByIdWithUser(@Param("commentId") Long commentId);

    @Query(" select c from CommentEntity c join fetch c.user where c.id = :commentId and c.notice.id = :noticeId")
    Optional<CommentEntity> findByIdAndNoticeIdWithUser(Long commentId, Long noticeId);



    // 공지(noticeId)에 속한 댓글을 페이지 단위로 조회 (기본 파생 쿼리)
    Page<CommentEntity> findByNoticeId(Long noticeId, Pageable pageable);

    // (선택) 댓글 + 작성자까지 한 번에 필요할 때 N+1 방지용 fetch join 버전
    @Query(
            value = "select c from CommentEntity c join fetch c.user where c.notice.id = :noticeId",
            countQuery = "select count(c) from CommentEntity c where c.notice.id = :noticeId"
    )
    Page<CommentEntity> findByNoticeIdWithUser(Long noticeId, Pageable pageable);



    /**
     * 특정 공지(noticeId)에 달린 댓글(CommentEntity)을 페이지 단위로 조회하는 메서드.
     * - fetch join을 사용해 Comment와 User를 한 번에 가져옴 (N+1 문제 방지).
     * - Page<CommentEntity>로 반환되므로 totalCount(총 댓글 수)와 페이지네이션 정보까지 제공됨.
     * - Pageable 파라미터(page, size, sort)를 받아서 쿼리에 자동 반영됨.
     * - Page 객체 안에 content(댓글 목록), totalElements(총 개수), totalPages(총 페이지 수) 등을 포함.
     */
}
