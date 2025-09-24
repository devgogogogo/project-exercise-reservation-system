package com.fastcampus.exercisereservationsystem.domain.user.repository;

import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

}
