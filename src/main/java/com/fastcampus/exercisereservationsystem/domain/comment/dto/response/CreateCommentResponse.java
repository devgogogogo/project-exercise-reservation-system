package com.fastcampus.exercisereservationsystem.domain.comment.dto.response;

import com.fastcampus.exercisereservationsystem.domain.comment.entity.CommentEntity;

import java.time.LocalDate;

public record CreateCommentResponse(
        Long commentId,
        String username,
        String description,
        LocalDate modifiedAt
) {

    public static CreateCommentResponse from(CommentEntity entity) {
        return new CreateCommentResponse(
                entity.getId(),
                entity.getUser().getUsername(),
                entity.getDescription(),
                entity.getModifiedAt().toLocalDate()
        );
    }
}
