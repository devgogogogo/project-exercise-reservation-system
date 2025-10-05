package com.fastcampus.exercisereservationsystem.domain.user.repository;

import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @EntityGraph(attributePaths = "role") // 실제 필드명에 맞게 수정
    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByNickname(String nickname);
}
