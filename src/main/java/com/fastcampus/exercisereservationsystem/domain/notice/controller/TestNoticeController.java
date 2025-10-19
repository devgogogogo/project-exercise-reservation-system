package com.fastcampus.exercisereservationsystem.domain.notice.controller;

import com.fastcampus.exercisereservationsystem.domain.notice.dto.response.GetNoticeResponse;
import com.fastcampus.exercisereservationsystem.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestNoticeController {

    private final NoticeService noticeService;

    @GetMapping("/General/{noticeId}")
    public ResponseEntity<GetNoticeResponse> getById(@PathVariable Long noticeId) {
        GetNoticeResponse response = noticeService.getById(noticeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/EntityGraph/{noticeId}")
    public ResponseEntity<GetNoticeResponse> getByEntityGraph(@PathVariable Long noticeId) {
        GetNoticeResponse response = noticeService.getByEntityGraph(noticeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/Query/{noticeId}")
    public ResponseEntity<GetNoticeResponse> getByQuery(@PathVariable Long noticeId) {
        GetNoticeResponse response = noticeService.getByQuery(noticeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/redis/{noticeId}")
    public ResponseEntity<GetNoticeResponse> getByRedis(@PathVariable Long noticeId) {
        GetNoticeResponse response = noticeService.getByRedis(noticeId);
        return ResponseEntity.ok(response);
    }
}
