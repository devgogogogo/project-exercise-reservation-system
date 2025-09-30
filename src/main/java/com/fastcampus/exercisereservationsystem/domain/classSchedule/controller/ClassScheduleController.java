package com.fastcampus.exercisereservationsystem.domain.classSchedule.controller;

import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.response.GetClassScheduleResponse;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.service.ClassScheduleService;
import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/classSchedules")
@RequiredArgsConstructor
public class ClassScheduleController {

    private final ClassScheduleService classScheduleService;

    @GetMapping
    public ResponseEntity<List<GetClassScheduleResponse>> getByDate(
            @AuthenticationPrincipal UserEntity userEntity,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<GetClassScheduleResponse> responses = classScheduleService.getByDate(userEntity, date);
        return ResponseEntity.ok().body(responses);
    }
}
