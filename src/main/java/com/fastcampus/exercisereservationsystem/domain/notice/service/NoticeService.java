package com.fastcampus.exercisereservationsystem.domain.notice.service;

import com.fastcampus.exercisereservationsystem.common.exception.BizException;
import com.fastcampus.exercisereservationsystem.domain.notice.dto.request.CreateNoticeRequest;
import com.fastcampus.exercisereservationsystem.domain.notice.dto.request.UpdateNoticeRequest;
import com.fastcampus.exercisereservationsystem.domain.notice.dto.response.CreateNoticeResponse;
import com.fastcampus.exercisereservationsystem.domain.notice.dto.response.GetNoticeListResponse;
import com.fastcampus.exercisereservationsystem.domain.notice.dto.response.GetNoticeResponse;
import com.fastcampus.exercisereservationsystem.domain.notice.dto.response.UpdateNoticeResponse;
import com.fastcampus.exercisereservationsystem.domain.notice.entity.NoticeEntity;
import com.fastcampus.exercisereservationsystem.domain.notice.exception.NoticeErrorCode;
import com.fastcampus.exercisereservationsystem.domain.notice.repository.NoticeRepository;
import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import com.fastcampus.exercisereservationsystem.domain.user.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;

    //공지사항 생성
    @Transactional
    public CreateNoticeResponse createNotice(CreateNoticeRequest request,UserEntity userEntity) {
        NoticeEntity noticeEntity = new NoticeEntity(request.title(), request.description(), userEntity);
        noticeRepository.save(noticeEntity);
        return CreateNoticeResponse.from(noticeEntity);
    }

    //공지사항 전체조회
    @Transactional(readOnly = true)
    public List<GetNoticeListResponse> getNoticeList() {
        List<NoticeEntity> noticeList = noticeRepository.findAll();
        return noticeList.stream().map(noticeEntity -> GetNoticeListResponse.from(noticeEntity)).toList();
    }

    //공지사항 단건 조회
    @Transactional(readOnly = true)
    public GetNoticeResponse getNotice(Long noticeId) {
        NoticeEntity noticeEntity = noticeRepository.findByIdWithUser(noticeId).orElseThrow(() ->new BizException(NoticeErrorCode.NOTICE_NOT_FOUND));
        return new GetNoticeResponse(noticeEntity.getId(),noticeEntity.getUser().getUsername(), noticeEntity.getTitle(), noticeEntity.getDescription());
    }

    //공지사항 수정
    @Transactional
    public UpdateNoticeResponse updateNotice(UserEntity userEntity,UpdateNoticeRequest request, Long noticeId) {
        NoticeEntity noticeEntity = noticeRepository.findByIdWithUser(noticeId).orElseThrow(() ->new BizException(NoticeErrorCode.NOTICE_NOT_FOUND));
        if (!userEntity.getId().equals(noticeEntity.getUser().getId())) {
            throw new BizException(UserErrorCode.USER_NOT_OWNER);
        }
        noticeEntity.updateNotice(request.title(), request.description());
        noticeRepository.save(noticeEntity);
        return UpdateNoticeResponse.from(noticeEntity); //DTO 안에 유저를 가지고 오는게 있어서 LazyInitializationException 예외가 터짐
    }

    @Transactional
    public void deleteNotice(UserEntity userEntity,Long noticeId) {
        NoticeEntity noticeEntity = noticeRepository.findById(noticeId).orElseThrow(() ->new BizException(NoticeErrorCode.NOTICE_NOT_FOUND));
        if (!userEntity.getId().equals(noticeEntity.getUser().getId())) {
            throw new BizException(UserErrorCode.USER_NOT_OWNER);
        }
        noticeRepository.delete(noticeEntity);
    }
}
