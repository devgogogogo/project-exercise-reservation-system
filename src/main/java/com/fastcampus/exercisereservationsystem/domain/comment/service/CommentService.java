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
import com.fastcampus.exercisereservationsystem.domain.notice.exception.NoticeErrorCode;
import com.fastcampus.exercisereservationsystem.domain.notice.repository.NoticeRepository;
import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import com.fastcampus.exercisereservationsystem.domain.user.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final NoticeRepository noticeRepository;

    @Transactional
    public CreateCommentResponse createComment(UserEntity userEntity, Long noticeId, CreateCommentRequest request) {
        NoticeEntity noticeEntity = noticeRepository.findByIdWithUser(noticeId).orElseThrow(() -> new BizException(NoticeErrorCode.NOTICE_NOT_FOUND));
        CommentEntity commentEntity = new CommentEntity(noticeEntity, userEntity, request.description());
        commentRepository.save(commentEntity);
        return CreateCommentResponse.from(commentEntity);
    }

    @Transactional(readOnly = true)
    public List<GetCommentListResponse> getCommentList(Long noticeId) {
        List<CommentEntity> commentList = commentRepository.findByNoticeIdWithUser(noticeId);
        return commentList.stream().map(comment -> GetCommentListResponse.from(comment)).toList();
    }
    @Transactional
    public UpdateCommentResponse updateComment(UserEntity userEntity,Long noticeId, Long commentId, UpdateCommentRequest request) {
        CommentEntity commentEntity = commentRepository.findByIdAndNoticeIdWithUser(commentId, noticeId).orElseThrow(() -> new BizException(CommentErrorCode.COMMENT_NOT_FOUND));
        if (userEntity.getId().equals(commentEntity.getUser().getId())) {
            commentEntity.updateComment(request.description());
            return UpdateCommentResponse.from(commentEntity);
        } else {
            throw new BizException(CommentErrorCode.COMMENT_FORBIDDEN);
        }
    }
    @Transactional
    public void deleteComment(UserEntity userEntity,Long noticeId, Long commentId) {
        CommentEntity commentEntity = commentRepository.findByIdAndNoticeIdWithUser(commentId, noticeId).orElseThrow(() -> new BizException(CommentErrorCode.COMMENT_NOT_FOUND));
        if (userEntity.getId().equals(commentEntity.getUser().getId())) {
             commentRepository.deleteById(commentId);
        } else {
            throw new BizException(UserErrorCode.USER_NOT_OWNER);
        }
    }
}
