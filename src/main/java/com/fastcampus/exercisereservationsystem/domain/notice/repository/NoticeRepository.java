package com.fastcampus.exercisereservationsystem.domain.notice.repository;

import com.fastcampus.exercisereservationsystem.domain.notice.entity.NoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<NoticeEntity, Long> {

}
