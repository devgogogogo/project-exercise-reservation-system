package com.fastcampus.exercisereservationsystem.domain.notice.controller;

import com.fastcampus.exercisereservationsystem.domain.notice.dto.response.GetNoticeResponse;
import com.fastcampus.exercisereservationsystem.domain.notice.service.TestNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestNoticeController {

    private final TestNoticeService testNoticeService;

    @GetMapping("/General")  //✅
    public ResponseEntity<List<GetNoticeResponse>> getList() {
        List<GetNoticeResponse>response = testNoticeService.getList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/EntityGraph") //✅
    public ResponseEntity<List<GetNoticeResponse>> getListEntityGraph(
            @RequestParam String titleKeyword) {
        List<GetNoticeResponse> response = testNoticeService.getListEntityGraph(titleKeyword);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/Query")
    public ResponseEntity<Page<GetNoticeResponse>> getByQuery(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<GetNoticeResponse> response = testNoticeService.getByQuery(page, size);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/redis")
    public ResponseEntity<Page<GetNoticeResponse>> getByRedisPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<GetNoticeResponse> response = testNoticeService.getByRedisPage(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/redis/{noticeId}")
    public ResponseEntity<GetNoticeResponse> getByRedis(@PathVariable Long noticeId) {
        GetNoticeResponse response = testNoticeService.getByRedis(noticeId);
        return ResponseEntity.ok(response);
    }
}
