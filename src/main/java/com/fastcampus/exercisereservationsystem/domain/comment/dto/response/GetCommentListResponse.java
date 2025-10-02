package com.fastcampus.exercisereservationsystem.domain.comment.dto.response;

import com.fastcampus.exercisereservationsystem.domain.comment.entity.CommentEntity;

import java.time.LocalDate;

public record GetCommentListResponse(
        Long commentId,
        String description,
        LocalDate modifiedAt
) {
    public static GetCommentListResponse from(CommentEntity entity) {

        return new GetCommentListResponse(
                entity.getId(),
                entity.getDescription(),
                entity.getModifiedAt().toLocalDate()
        );
    }
}
