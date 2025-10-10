package com.fastcampus.exercisereservationsystem.domain.program.entity;

import com.fastcampus.exercisereservationsystem.common.BaseEntity;
import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Table(name = "program")
@Entity
public class ProgramEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @Column(nullable = false)
    private String exerciseName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String exerciseDescription;

    @Column(nullable = false)
    private LocalDate date;

    public ProgramEntity(UserEntity userEntity, String exerciseName, String exerciseDescription, LocalDate date) {
        this.userEntity = userEntity;
        this.exerciseName = exerciseName;
        this.exerciseDescription = exerciseDescription;
        this.date = date;
    }

    public void updateProgramEntity(String exerciseName, String exerciseDescription, LocalDate date) {
        this.exerciseName = exerciseName;
        this.exerciseDescription = exerciseDescription;
        this.date = date;
    }
}
