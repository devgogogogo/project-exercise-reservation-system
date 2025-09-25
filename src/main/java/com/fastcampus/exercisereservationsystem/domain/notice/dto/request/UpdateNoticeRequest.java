package com.fastcampus.exercisereservationsystem.domain.notice.dto.request;

public record UpdateNoticeRequest(
        String title,
        String description
) {
}
