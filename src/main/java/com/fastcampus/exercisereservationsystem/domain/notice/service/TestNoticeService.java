package com.fastcampus.exercisereservationsystem.domain.notice.service;

import com.fastcampus.exercisereservationsystem.common.exception.BizException;
import com.fastcampus.exercisereservationsystem.domain.notice.dto.response.GetNoticeResponse;
import com.fastcampus.exercisereservationsystem.domain.notice.entity.NoticeEntity;
import com.fastcampus.exercisereservationsystem.domain.notice.exception.NoticeErrorCode;
import com.fastcampus.exercisereservationsystem.domain.notice.repository.NoticeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TestNoticeService {

    private final NoticeRepository noticeRepository;
    private final RedisTemplate<String, String> redisTemplate;

    // ✅ ObjectMapper (JSON 직렬화/역직렬화용)
    private final ObjectMapper om = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule()) // LocalDateTime 대응
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


    //todo : List 전체 조회 test(일반) findAll() ✅ TestNoticeController
    @Transactional(readOnly = true)
    public List<GetNoticeResponse> getList() {
        List<NoticeEntity> noticeEntities = noticeRepository.findAll();
        return noticeEntities.stream().map(GetNoticeResponse::from).toList();
    }

    //todo : List @EntityGraph 이용한 조회 test (N+1)✅
    @Transactional(readOnly = true)
    public  List<GetNoticeResponse> getListEntityGraph(String titleKeyword) {
        List<NoticeEntity> noticeEntities = noticeRepository.findByTitleContainingIgnoreCase(titleKeyword);
        return noticeEntities.stream().map(GetNoticeResponse::from).toList();
    }

    //todo : Page @Query(fetch join) 이용한 조회 test (N+1) ✅
    @Transactional(readOnly = true)
    public Page<GetNoticeResponse> getByQuery(int page, int size) {
        int safePage = Math.max(1, page) -1;
        int safeSize = Math.max(1, size);
        PageRequest pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<NoticeEntity> noticeEntities = noticeRepository.findAllWithUserByQuery(pageable);
        return noticeEntities.map(GetNoticeResponse::from);
    }

    //todo : Page redis를 이용한 페이지 조회
    @Transactional(readOnly = true)
    public Page<GetNoticeResponse> getByRedisPage(int page, int size) {
        PageRequest pr = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        String key = String.format("notices:all:p:%d:s:%d", page, size);

        // 1) 캐시 조회
        String cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            try {
                com.fasterxml.jackson.databind.JsonNode node = om.readTree(cached);
                List<GetNoticeResponse> content =
                        om.convertValue(node.get("content"), new com.fasterxml.jackson.core.type.TypeReference<List<GetNoticeResponse>>() {});
                int pageNumber = node.get("pageNumber").asInt();
                int pageSize   = node.get("pageSize").asInt();
                long totalElements = node.get("totalElements").asLong();

                return new org.springframework.data.domain.PageImpl<>(content, PageRequest.of(pageNumber, pageSize), totalElements);
            } catch (Exception e) {
                System.out.println("캐시 역직렬화 실패: " + e.getMessage());
            }
        }

        // 2) DB 조회 (fetch join + count)
        Page<NoticeEntity> pageEntities = noticeRepository.findPageWithUser(pr);
        Page<GetNoticeResponse> result = pageEntities.map(GetNoticeResponse::from);

        // 3) 캐시 저장 (Map 형태로 직렬화)
        try {
            var root = om.createObjectNode();
            root.set("content", om.valueToTree(result.getContent()));
            root.put("totalElements", result.getTotalElements());
            root.put("totalPages", result.getTotalPages());
            root.put("pageNumber", result.getNumber());
            root.put("pageSize", result.getSize());

            String json = om.writeValueAsString(root);
            redisTemplate.opsForValue().set(key, json, java.time.Duration.ofMinutes(10));
        } catch (Exception e) {
            System.out.println("캐시 저장 실패: " + e.getMessage());
        }

        return result;
    }

    //todo : Page redis 를 활용한 단일조회
    @Transactional(readOnly = true)
    public GetNoticeResponse getByRedis(Long noticeId) {
        String key = "notice:" + noticeId;

        // 1️⃣ 캐시 조회
        String cashed = redisTemplate.opsForValue().get(key);
        if (cashed != null) { //값이 있으면 리턴해줘라
            try {
                return om.readValue(cashed, GetNoticeResponse.class);
            } catch (Exception e) {
                System.out.println("캐시 역직렬화 실패: " + e.getMessage());
            }
        }
        // 2️⃣ DB 조회 (N+1 해결 쿼리)
        NoticeEntity noticeEntity = noticeRepository.findByIdWithQuery(noticeId).orElseThrow(() -> new BizException(NoticeErrorCode.NOTICE_NOT_FOUND));
        GetNoticeResponse dto = GetNoticeResponse.from(noticeEntity);

        // 3️⃣ 캐시 저장
        try {
            String json = om.writeValueAsString(dto);
            redisTemplate.opsForValue().set(key, json, Duration.ofMinutes(10));
        } catch (Exception e) {
            System.out.println("캐시 저장 실패: " + e.getMessage());
        }
        return dto;
    }
}
