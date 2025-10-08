package com.fastcampus.exercisereservationsystem.domain.program.controller;

import com.fastcampus.exercisereservationsystem.domain.program.dto.request.CreateProgramRequest;
import com.fastcampus.exercisereservationsystem.domain.program.dto.request.UpdateProgramRequest;
import com.fastcampus.exercisereservationsystem.domain.program.dto.response.CreateProgramResponse;
import com.fastcampus.exercisereservationsystem.domain.program.dto.response.GetByDateProgramResponse;
import com.fastcampus.exercisereservationsystem.domain.program.dto.response.UpdateProgramResponse;
import com.fastcampus.exercisereservationsystem.domain.program.service.ProgramService;
import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@EnableMethodSecurity(prePostEnabled = true)
@RestController
@RequestMapping("/api/program")
@RequiredArgsConstructor
public class ProgramController {

    private final ProgramService programService;

    //프로그램 생성
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CreateProgramResponse> createProgram(
            @RequestBody CreateProgramRequest createProgramRequest,
            @AuthenticationPrincipal UserEntity userEntity) {
        CreateProgramResponse response = programService.createProgram(createProgramRequest, userEntity);
        return ResponseEntity.ok(response);
    }

    // 날짜 선택으로 조회
    @GetMapping()
    public ResponseEntity<List<GetByDateProgramResponse>> getByDateProgram(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<GetByDateProgramResponse> responseList = programService.getByDateProgram(date);
        return ResponseEntity.ok(responseList);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{programId}")
    public ResponseEntity<UpdateProgramResponse> dateProgram(
            @PathVariable Long programId,
            @RequestBody UpdateProgramRequest request
    ) {
        UpdateProgramResponse response = programService.updateProgram(programId, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{programId}")
    public ResponseEntity<Void> deleteProgram(@PathVariable Long programId) {
        programService.deleteProgram(programId);
        return ResponseEntity.noContent().build();
    }
}
