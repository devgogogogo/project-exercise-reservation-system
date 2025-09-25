package com.fastcampus.exercisereservationsystem.domain.notice.dto.response;

import com.fastcampus.exercisereservationsystem.domain.notice.entity.NoticeEntity;

public record GetNoticeResponse(
        Long noticeId,
        String username,
        String title,
        String description
) {
    public static GetNoticeResponse from(NoticeEntity entity) {
        return new GetNoticeResponse(
                entity.getId(),
                entity.getUser().getUsername(),
                entity.getTitle(),
                entity.getDescription()
        );
    }
}
