package com.fastcampus.exercisereservationsystem.domain.notice.dto.response;

import com.fastcampus.exercisereservationsystem.domain.notice.entity.NoticeEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record GetNoticeResponse(
        Long noticeId,
        String nickname,
        String title,
        String description,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm")
        LocalDateTime modifiedAt
) {
    public static GetNoticeResponse from(NoticeEntity entity) {
        return new GetNoticeResponse(
                entity.getId(),
                entity.getUser().getNickname(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getModifiedAt()
        );
    }
}
