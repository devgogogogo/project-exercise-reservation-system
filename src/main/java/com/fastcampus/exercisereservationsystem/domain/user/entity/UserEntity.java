package com.fastcampus.exercisereservationsystem.domain.user.entity;

import com.fastcampus.exercisereservationsystem.common.BaseEntity;
import com.fastcampus.exercisereservationsystem.common.enums.Authority;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Table(name = "users")
@Entity
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Authority authority;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate startAt; //회원등록 시작일

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate endAt; //회원등록 종료일


    public static UserEntity of(String name, String username, String password, LocalDate startAt, LocalDate endAt) {
        UserEntity user = new UserEntity();
        user.name = name;
        user.username = username;
        user.password = password;
        user.authority = Authority.USER;
        user.startAt = startAt;
        user.endAt = endAt;
        return user;
    }

    public void updatePeriod(LocalDate newStartAt, LocalDate newEndAt) {
        this.startAt = newStartAt;
        this.endAt = newEndAt;
    }
}
