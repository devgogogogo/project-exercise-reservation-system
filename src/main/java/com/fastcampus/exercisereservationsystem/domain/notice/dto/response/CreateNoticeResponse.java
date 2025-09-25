package com.fastcampus.exercisereservationsystem.domain.notice.dto.response;

import com.fastcampus.exercisereservationsystem.domain.notice.entity.NoticeEntity;

public record CreateNoticeResponse(
        Long id,
        String title,
        String description
) {
    public static CreateNoticeResponse from(NoticeEntity entity) {
        return new CreateNoticeResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription()
        );
    }
}
