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
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final RedisTemplate<String,String> redisTemplate;

    // ✅ ObjectMapper (JSON 직렬화/역직렬화용)
    private final ObjectMapper om = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule()) // LocalDateTime 대응
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    //공지사항 생성
    @Transactional
    public CreateNoticeResponse createNotice(CreateNoticeRequest request,UserEntity userEntity) {
        NoticeEntity noticeEntity = new NoticeEntity(request.title(), request.description(), userEntity);
        noticeRepository.save(noticeEntity);
        return CreateNoticeResponse.from(noticeEntity);
    }

    //todo : page 일반조회 ✅ 응답속도 test
    @Transactional(readOnly = true)
    public Page<GetNoticeListResponse> getNoticeList(int page, int size) {
        int safePage = Math.max(1, page) -1;
        int safeSize = Math.max(1, size);
        PageRequest pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<NoticeEntity> noticeList = noticeRepository.findAll(pageable);
        return noticeList.map(GetNoticeListResponse::from);
    }

    //todo : page @EntityGraph 이용한 조회 응답속도 test ✅
    @Transactional(readOnly = true)
    public Page<GetNoticeListResponse> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String keyword, int page, int size) {
        int safePage = Math.max(1, page) - 1;
        int safeSize = Math.max(1, size);
        PageRequest pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<NoticeEntity> result = noticeRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword, pageable);
        return result.map(GetNoticeListResponse::from);
    }

    @Transactional(readOnly = true)
    public GetNoticeResponse getNotice(Long noticeId) {
        NoticeEntity noticeEntity = noticeRepository.findByIdWithUser(noticeId).orElseThrow(() -> new BizException(NoticeErrorCode.NOTICE_NOT_FOUND));
        return new GetNoticeResponse(noticeEntity.getId(), noticeEntity.getUser().getNickname(), noticeEntity.getTitle(), noticeEntity.getDescription(),noticeEntity.getModifiedAt());
    }

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
