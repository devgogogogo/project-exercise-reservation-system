package com.fastcampus.exercisereservationsystem.domain.notice.dto.response;

import com.fastcampus.exercisereservationsystem.domain.notice.entity.NoticeEntity;

public record GetNoticeListResponse(
        Long noticeId,
        String title
) {
    public static GetNoticeListResponse from(NoticeEntity entity) {
        return new GetNoticeListResponse(entity.getId(),entity.getTitle());
    }
}
