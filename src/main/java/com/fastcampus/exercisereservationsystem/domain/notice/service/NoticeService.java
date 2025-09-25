package com.fastcampus.exercisereservationsystem.domain.notice.service;

import com.fastcampus.exercisereservationsystem.domain.notice.dto.request.CreateNoticeRequest;
import com.fastcampus.exercisereservationsystem.domain.notice.dto.request.UpdateNoticeRequest;
import com.fastcampus.exercisereservationsystem.domain.notice.dto.response.CreateNoticeResponse;
import com.fastcampus.exercisereservationsystem.domain.notice.dto.response.GetNoticeListResponse;
import com.fastcampus.exercisereservationsystem.domain.notice.dto.response.GetNoticeResponse;
import com.fastcampus.exercisereservationsystem.domain.notice.dto.response.UpdateNoticeResponse;
import com.fastcampus.exercisereservationsystem.domain.notice.entity.NoticeEntity;
import com.fastcampus.exercisereservationsystem.domain.notice.repository.NoticeRepository;
import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import com.fastcampus.exercisereservationsystem.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    //공지사항 생성
    public CreateNoticeResponse createNotice(CreateNoticeRequest request, Long userId) {

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("유저가 없습니다."));

        NoticeEntity noticeEntity = new NoticeEntity(request.title(), request.description(), userEntity);

        noticeRepository.save(noticeEntity);
        return CreateNoticeResponse.from(noticeEntity);
    }

    //공지사항 전체조회
    public List<GetNoticeListResponse> getNoticeList() {
        List<NoticeEntity> noticeList = noticeRepository.findAll();
        return noticeList.stream().map(noticeEntity -> GetNoticeListResponse.from(noticeEntity)).toList();
    }

    //공지사항 단건 조회
    public GetNoticeResponse getNotice(Long noticeId) {
        NoticeEntity noticeEntity = noticeRepository.findById(noticeId).orElseThrow(() -> new EntityNotFoundException("공지사항이 없습니다."));
        return new GetNoticeResponse(noticeEntity.getId(),noticeEntity.getUser().getUsername(), noticeEntity.getTitle(), noticeEntity.getDescription());
    }


    //공지사항 수정
    public UpdateNoticeResponse updateNotice(UpdateNoticeRequest request, Long noticeId) {
        NoticeEntity noticeEntity = noticeRepository.findById(noticeId).orElseThrow(() -> new EntityNotFoundException("공지사항이 없습니다."));
        noticeEntity.updateNotice(request.title(), request.description());
        noticeRepository.save(noticeEntity);
        return UpdateNoticeResponse.from(noticeEntity);
    }


    public void deleteNotice(Long noticeId) {
        NoticeEntity noticeEntity = noticeRepository.findById(noticeId).orElseThrow(() -> new EntityNotFoundException("공지사항이 없습니다."));
        noticeRepository.delete(noticeEntity);
    }
}
