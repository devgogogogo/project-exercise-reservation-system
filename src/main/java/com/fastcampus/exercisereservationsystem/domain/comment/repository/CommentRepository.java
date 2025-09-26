package com.fastcampus.exercisereservationsystem.domain.comment.repository;

import com.fastcampus.exercisereservationsystem.domain.comment.entity.CommentEntity;
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

    List<CommentEntity> findByNoticeId(Long noticeId);
}
