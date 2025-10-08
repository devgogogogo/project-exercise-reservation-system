package com.fastcampus.exercisereservationsystem.domain.program.service;

import com.fastcampus.exercisereservationsystem.common.exception.BizException;
import com.fastcampus.exercisereservationsystem.domain.program.dto.request.UpdateProgramRequest;
import com.fastcampus.exercisereservationsystem.domain.program.dto.request.CreateProgramRequest;
import com.fastcampus.exercisereservationsystem.domain.program.dto.response.CreateProgramResponse;
import com.fastcampus.exercisereservationsystem.domain.program.dto.response.GetByDateProgramResponse;
import com.fastcampus.exercisereservationsystem.domain.program.dto.response.UpdateProgramResponse;
import com.fastcampus.exercisereservationsystem.domain.program.entity.ProgramEntity;
import com.fastcampus.exercisereservationsystem.domain.program.exception.ProgramErrorCode;
import com.fastcampus.exercisereservationsystem.domain.program.repository.ProgramRepository;
import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgramService {
    private final ProgramRepository programRepository;

    @Transactional
    public CreateProgramResponse createProgram(CreateProgramRequest request, UserEntity userEntity) {
        ProgramEntity programEntity = new ProgramEntity(userEntity, request.exerciseName(), request.exerciseDescription(), request.date());
        ProgramEntity save = programRepository.save(programEntity);
        return CreateProgramResponse.from(save);
    }

    @Transactional(readOnly = true)
    public List<GetByDateProgramResponse> getByDateProgram(LocalDate date) {
        List<ProgramEntity> programEntities = programRepository.findByDate(date);
        return programEntities.stream().map(GetByDateProgramResponse::from).toList();
    }

    @Transactional
    public UpdateProgramResponse updateProgram(Long programId,UpdateProgramRequest request) {

        ProgramEntity programEntity = programRepository.findById(programId).orElseThrow(() -> new BizException(ProgramErrorCode.PROGRAM_NOT_FOUND));

        programEntity.updateProgramEntity(request.exerciseName(), request.exerciseDescription(), request.date());
        programRepository.save(programEntity);
        return UpdateProgramResponse.from(programEntity);
    }


    public void deleteProgram(Long programId) {
        ProgramEntity programEntity = programRepository.findById(programId).orElseThrow(() -> new BizException(ProgramErrorCode.PROGRAM_NOT_FOUND));
        programRepository.delete(programEntity);
    }
}
