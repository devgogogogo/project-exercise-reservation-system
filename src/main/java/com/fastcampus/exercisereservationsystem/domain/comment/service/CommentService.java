package com.fastcampus.exercisereservationsystem.domain.comment.service;

import com.fastcampus.exercisereservationsystem.domain.comment.dto.request.CreateCommentRequest;
import com.fastcampus.exercisereservationsystem.domain.comment.dto.request.UpdateCommentRequest;
import com.fastcampus.exercisereservationsystem.domain.comment.dto.response.CreateCommentResponse;
import com.fastcampus.exercisereservationsystem.domain.comment.dto.response.GetCommentListResponse;
import com.fastcampus.exercisereservationsystem.domain.comment.dto.response.UpdateCommentResponse;
import com.fastcampus.exercisereservationsystem.domain.comment.entity.CommentEntity;
import com.fastcampus.exercisereservationsystem.domain.comment.repository.CommentRepository;
import com.fastcampus.exercisereservationsystem.domain.notice.entity.NoticeEntity;
import com.fastcampus.exercisereservationsystem.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final NoticeRepository noticeRepository;


    @Transactional
    public CreateCommentResponse createComment(Long noticeId, CreateCommentRequest request) {
        NoticeEntity noticeEntity = noticeRepository.findByIdWithUser(noticeId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "공지사항이 존재하지 않아 댓글을 달 수 없습니다."));
        CommentEntity commentEntity = new CommentEntity(noticeEntity, noticeEntity.getUser(), request.description());
        commentRepository.save(commentEntity);
        return CreateCommentResponse.from(commentEntity);
    }

    @Transactional(readOnly = true)
    public List<GetCommentListResponse> getCommentList(Long noticeId) {
        noticeRepository.findById(noticeId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 공지가 존재하지 않습니다."));
        List<CommentEntity> commentList = commentRepository.findByNoticeIdWithUser(noticeId);
        return commentList.stream().map(comment -> GetCommentListResponse.from(comment)).toList();
    }

    public UpdateCommentResponse updateComment(Long noticeId, Long commentId, UpdateCommentRequest request) {
        noticeRepository.findById(noticeId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 공지가 존재하지 않습니다."));
        CommentEntity commentEntity = commentRepository.findByIdWithUser(commentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "수정할 댓글이 존재하지 않습니다."));
        commentEntity.updateComment(request.description());
        commentRepository.save(commentEntity);
        return UpdateCommentResponse.from(commentEntity);
    }

    public void deleteComment(Long noticeId, Long commentId) {
        noticeRepository.findById(noticeId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 공지가 존재하지 않습니다."));
        CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "삭제할 댓글이 존재 하지 않습니다."));
        commentRepository.delete(commentEntity);
    }
}
