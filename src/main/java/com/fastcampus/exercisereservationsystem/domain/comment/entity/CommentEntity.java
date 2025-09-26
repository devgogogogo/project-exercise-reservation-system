package com.fastcampus.exercisereservationsystem.domain.comment.entity;

import com.fastcampus.exercisereservationsystem.common.BaseEntity;
import com.fastcampus.exercisereservationsystem.domain.notice.entity.NoticeEntity;
import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@Table(name = "comments")
@Entity
public class CommentEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @JoinColumn(name = "notice_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private NoticeEntity notice;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    private String description;

    public CommentEntity(NoticeEntity notice, UserEntity user, String description) {
        this.notice = notice;
        this.user = user;
        this.description = description;
    }

    public void updateComment(String description) {
        this.description = description;
    }
}
