package com.fastcampus.exercisereservationsystem.domain.notice.controller;

import com.fastcampus.exercisereservationsystem.domain.notice.dto.request.CreateNoticeRequest;
import com.fastcampus.exercisereservationsystem.domain.notice.dto.request.UpdateNoticeRequest;
import com.fastcampus.exercisereservationsystem.domain.notice.dto.response.CreateNoticeResponse;
import com.fastcampus.exercisereservationsystem.domain.notice.dto.response.GetNoticeListResponse;
import com.fastcampus.exercisereservationsystem.domain.notice.dto.response.GetNoticeResponse;
import com.fastcampus.exercisereservationsystem.domain.notice.dto.response.UpdateNoticeResponse;
import com.fastcampus.exercisereservationsystem.domain.notice.service.NoticeService;
import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService noticeService;

    //공지사항 생성
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<CreateNoticeResponse> createNotice(
            @RequestBody CreateNoticeRequest request,
            @AuthenticationPrincipal UserEntity userEntity) {
        CreateNoticeResponse response = noticeService.createNotice(request,userEntity);
        return ResponseEntity.ok().body(response);
    }

    //공지사항 전체 조회 (페이징처리)
    @GetMapping
    public ResponseEntity<List<GetNoticeListResponse>> getNoticeList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<GetNoticeListResponse> list = noticeService.getNoticeList(page,size);
        return ResponseEntity.ok().body(list);
    }

    //공지사항 단건 조회
    @GetMapping("/{noticeId}")
    public ResponseEntity<GetNoticeResponse> getNotice(@PathVariable Long noticeId) {
        GetNoticeResponse response = noticeService.getNotice(noticeId);
        return ResponseEntity.ok(response);
    }

    //공지사항 수정
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{noticeId}")
    public ResponseEntity<UpdateNoticeResponse> updateNotice(
            @AuthenticationPrincipal UserEntity userEntity,
            @RequestBody UpdateNoticeRequest request,
            @PathVariable Long noticeId) {
        UpdateNoticeResponse response = noticeService.updateNotice(userEntity,request, noticeId);
        return ResponseEntity.ok().body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Void> deleteNotice(
            @AuthenticationPrincipal UserEntity userEntity,
            @PathVariable Long noticeId) {
        noticeService.deleteNotice(userEntity,noticeId);
        return ResponseEntity.noContent().build();
    }
}
