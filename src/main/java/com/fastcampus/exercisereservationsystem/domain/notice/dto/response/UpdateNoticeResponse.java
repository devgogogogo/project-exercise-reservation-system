package com.fastcampus.exercisereservationsystem.domain.notice.dto.response;

import com.fastcampus.exercisereservationsystem.domain.notice.entity.NoticeEntity;

public record UpdateNoticeResponse(
        Long noticeId,
        String username,
        String title,
        String description
) {
    public static UpdateNoticeResponse from(NoticeEntity noticeEntity) {
        return new UpdateNoticeResponse(
                noticeEntity.getId(),
                noticeEntity.getUser().getUsername(),
                noticeEntity.getTitle(),
                noticeEntity.getDescription()
        );
    }
}
