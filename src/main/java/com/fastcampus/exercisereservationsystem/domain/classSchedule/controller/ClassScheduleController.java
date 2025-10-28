package com.fastcampus.exercisereservationsystem.domain.classSchedule.controller;

import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.request.CreateClassScheduleRequest;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.request.UpdateClassScheduleRequest;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.response.CreateClassScheduleResponse;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.response.GetClassScheduleResponse;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.response.UpdateClassScheduleResponse;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.service.ClassScheduleService;
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
@RequestMapping("/api/classSchedules")
@RequiredArgsConstructor
public class ClassScheduleController {

    private final ClassScheduleService classScheduleService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<CreateClassScheduleResponse> createClassSchedule(
            @AuthenticationPrincipal UserEntity userEntity,
            @RequestBody CreateClassScheduleRequest request) {
        CreateClassScheduleResponse response = classScheduleService.createClassSchedule(request,userEntity);
        return ResponseEntity.ok().body(response);
    }

    //이 api로 달력에서 조회됨
    @GetMapping("/calendar")
    public ResponseEntity<List<GetClassScheduleResponse>> getClassSchedules(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        List<GetClassScheduleResponse> responses = classScheduleService.getClassSchedules(start,end);
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    public ResponseEntity<List<GetClassScheduleResponse>> getByDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<GetClassScheduleResponse> responses = classScheduleService.getByDate(date);
        return ResponseEntity.ok().body(responses);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{classSchedulesId}")
    public ResponseEntity<UpdateClassScheduleResponse> updateClassSchedule(
            @PathVariable Long classSchedulesId,
            @RequestBody UpdateClassScheduleRequest request) {
        UpdateClassScheduleResponse response = classScheduleService.updateClassSchedule(request, classSchedulesId);
        return ResponseEntity.ok().body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{classSchedulesId}")
    public ResponseEntity<Void> deleteClassSchedule(@PathVariable Long classSchedulesId) {
        classScheduleService.deleteClassSchedule(classSchedulesId);
        return ResponseEntity.noContent().build();
    }


}
