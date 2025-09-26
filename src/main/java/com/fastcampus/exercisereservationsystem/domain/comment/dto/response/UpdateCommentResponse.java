package com.fastcampus.exercisereservationsystem.domain.comment.dto.response;

import com.fastcampus.exercisereservationsystem.domain.comment.entity.CommentEntity;

import java.time.LocalDate;

public record UpdateCommentResponse(
        Long commentId,
        String username,
        String description,
        LocalDate modifiedAt
) {
    public static UpdateCommentResponse from(CommentEntity entity) {
        return new UpdateCommentResponse(
                entity.getId(),
                entity.getUser().getUsername(),
                entity.getDescription(),
                entity.getModifiedAt().toLocalDate()
        );
    }
}
