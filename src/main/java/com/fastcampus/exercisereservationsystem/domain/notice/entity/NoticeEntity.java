package com.fastcampus.exercisereservationsystem.domain.notice.entity;

import com.fastcampus.exercisereservationsystem.common.BaseEntity;
import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Table(name = "notices")
@Entity
public class NoticeEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    public NoticeEntity(String title, String description, UserEntity user) {
        this.title = title;
        this.description = description;
        this.user = user;
    }
}
