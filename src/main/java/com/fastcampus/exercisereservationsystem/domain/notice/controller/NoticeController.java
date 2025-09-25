package com.fastcampus.exercisereservationsystem.domain.notice.controller;

import com.fastcampus.exercisereservationsystem.domain.notice.dto.request.CreateNoticeRequest;
import com.fastcampus.exercisereservationsystem.domain.notice.dto.request.UpdateNoticeRequest;
import com.fastcampus.exercisereservationsystem.domain.notice.dto.response.CreateNoticeResponse;
import com.fastcampus.exercisereservationsystem.domain.notice.dto.response.GetNoticeListResponse;
import com.fastcampus.exercisereservationsystem.domain.notice.dto.response.GetNoticeResponse;
import com.fastcampus.exercisereservationsystem.domain.notice.dto.response.UpdateNoticeResponse;
import com.fastcampus.exercisereservationsystem.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    private final NoticeService noticeService;

    //공지사항 생성
    @PostMapping("/{userId}")
    public ResponseEntity<CreateNoticeResponse> createNotice(@RequestBody CreateNoticeRequest request, @PathVariable Long userId) {
        CreateNoticeResponse response = noticeService.createNotice(request, userId);
        return ResponseEntity.ok().body(response);
    }

    //공지사항 전체 조회
    @GetMapping
    public ResponseEntity<List<GetNoticeListResponse>> getNoticeList() {
        List<GetNoticeListResponse> list = noticeService.getNoticeList();
        return ResponseEntity.ok().body(list);
    }

    //공지사항 단건 조회
    @GetMapping("/{noticeId}")
    public ResponseEntity<GetNoticeResponse> getNotice(@PathVariable Long noticeId) {
        GetNoticeResponse response = noticeService.getNotice(noticeId);
        return ResponseEntity.ok(response);
    }

    //공지사항 수정
    @PutMapping("/{noticeId}")
    public ResponseEntity<UpdateNoticeResponse> updateNotice(@RequestBody UpdateNoticeRequest request, @PathVariable Long noticeId) {
        UpdateNoticeResponse response = noticeService.updateNotice(request, noticeId);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return ResponseEntity.noContent().build();
    }
}
