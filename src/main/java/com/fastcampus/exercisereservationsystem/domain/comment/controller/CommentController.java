package com.fastcampus.exercisereservationsystem.domain.comment.controller;

import com.fastcampus.exercisereservationsystem.domain.comment.dto.request.CreateCommentRequest;
import com.fastcampus.exercisereservationsystem.domain.comment.dto.request.UpdateCommentRequest;
import com.fastcampus.exercisereservationsystem.domain.comment.dto.response.CreateCommentResponse;
import com.fastcampus.exercisereservationsystem.domain.comment.dto.response.GetCommentListResponse;
import com.fastcampus.exercisereservationsystem.domain.comment.dto.response.UpdateCommentResponse;
import com.fastcampus.exercisereservationsystem.domain.comment.service.CommentService;
import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notices/{noticeId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    //댓글 생성
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping
    public ResponseEntity<CreateCommentResponse> createComment(
            @AuthenticationPrincipal UserEntity userEntity,
            @PathVariable Long noticeId,
            @RequestBody CreateCommentRequest request) {
        CreateCommentResponse response = commentService.createComment(userEntity, noticeId, request);
        return ResponseEntity.ok().body(response);
    }

    //페이징 조회
    @GetMapping
    public ResponseEntity<Page<GetCommentListResponse>> getCommentPage(
            @PathVariable Long noticeId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<GetCommentListResponse> responses = commentService.getCommentPage(noticeId, page, size);
        return ResponseEntity.ok().body(responses);
    }

    //댓글 수정
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{commentId}")
    public ResponseEntity<UpdateCommentResponse> updateComment(
            @AuthenticationPrincipal UserEntity userEntity,
            @PathVariable Long noticeId,
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequest request) {
        UpdateCommentResponse response = commentService.updateComment(userEntity, noticeId, commentId, request);
        return ResponseEntity.ok().body(response);
    }

    //댓글 삭제
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal UserEntity userEntity,
            @PathVariable Long noticeId,
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(userEntity, noticeId, commentId);
        return ResponseEntity.noContent().build();
    }

}
