package com.fastcampus.exercisereservationsystem.domain.notice.dto.request;

public record CreateNoticeRequest(
        String title,
        String description
) {
}
