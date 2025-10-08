package com.fastcampus.exercisereservationsystem.domain.program.repository;

import com.fastcampus.exercisereservationsystem.domain.program.entity.ProgramEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ProgramRepository extends JpaRepository<ProgramEntity, Long> {

    List<ProgramEntity> findByDate(LocalDate date);


}
