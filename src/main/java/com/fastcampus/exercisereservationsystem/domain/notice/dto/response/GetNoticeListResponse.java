package com.fastcampus.exercisereservationsystem.domain.notice.dto.response;

import com.fastcampus.exercisereservationsystem.domain.notice.entity.NoticeEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record GetNoticeListResponse(
        Long noticeId,
        String title,
        String nickName,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate modifiedAt
) {
    public static GetNoticeListResponse from(NoticeEntity entity) {
        return new GetNoticeListResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getUser().getNickname(),
                entity.getCreatedAt().toLocalDate()
        );
    }
}
