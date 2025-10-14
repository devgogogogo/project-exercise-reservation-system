package com.fastcampus.exercisereservationsystem.domain.notice.repository;

import com.fastcampus.exercisereservationsystem.domain.notice.entity.NoticeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NoticeRepository extends JpaRepository<NoticeEntity, Long> {

    /**
     * findById로 해서 유저를 가져오게 했는 데 오류가 났음
     * JPA/Hibernate는 연관된 엔티티(UserEntity)를 fetch = LAZY로 설정해두면, 실제 DB 조회를 필요할 때(Session이 열려있을 때) 하려고 해.
     * 그런데 지금은 NoticeEntity 안에서 UserEntity를 가져오려고 했는데, 이미 트랜잭션(session)이 닫힌 뒤라 Hibernate가 DB에서 추가 조회를 못 한 거야.
     * 그래서 프록시 객체만 남아 있고, 실제 데이터 로딩을 하려다가 세션 없음 → LazyInitializationException이 터진 거지.
     */

    /**
     * 언제 자주 발생하나 ?
     * Service에서 엔티티를 꺼내옴 → Controller까지 엔티티 그대로 리턴 → Jackson(JSON 직렬화)에서 user.getUsername() 호출
     * → 그 시점엔 이미 세션 닫힘 → LazyInitializationException.
     * 즉, 컨트롤러에서 응답 객체를 만들 때 지연로딩 필드에 접근할 때 주로 터진다.
     *
     * @Query는 연관엔티티가 있을때 사용한다.
     */

    @Query("select n from NoticeEntity n join fetch n.user where n.id = :id")
    Optional<NoticeEntity> findByIdWithUser(@Param("id") Long id);



    //키워드 :  제목 + 내용에 부분 일치 (대소문자 무시)
    @EntityGraph(attributePaths = "user") // user 함께 로딩
    Page<NoticeEntity> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String titleKeyword,
            String descKeyword,
            Pageable pageable
    );
}
