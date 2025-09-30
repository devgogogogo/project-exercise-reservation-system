package com.fastcampus.exercisereservationsystem.domain.classSchedule.controller;

import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.request.CreateClassScheduleRequest;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.request.UpdateClassScheduleRequest;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.response.CreateClassScheduleResponse;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.response.UpdateClassScheduleResponse;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.service.ClassScheduleService;
import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/classSchedules")
@RequiredArgsConstructor
public class ClassScheduleAdminController {
    private final ClassScheduleService classScheduleService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<CreateClassScheduleResponse> createClassSchedule(
            @AuthenticationPrincipal UserEntity userEntity,
            @RequestBody CreateClassScheduleRequest request) {
        CreateClassScheduleResponse response = classScheduleService.createClassSchedule(request,userEntity);
        return ResponseEntity.ok().body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{classSchedulesId}")
    public ResponseEntity<UpdateClassScheduleResponse> updateClassSchedule(
            @AuthenticationPrincipal UserEntity userEntity,
            @PathVariable Long classSchedulesId,
            @RequestBody UpdateClassScheduleRequest request) {
        UpdateClassScheduleResponse response = classScheduleService.updateClassSchedule(userEntity, request, classSchedulesId);
        return ResponseEntity.ok().body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{classSchedulesId}")
    public ResponseEntity<Void> deleteClassSchedule(
            @AuthenticationPrincipal UserEntity userEntity,
            @PathVariable Long classSchedulesId) {
        classScheduleService.deleteClassSchedule(userEntity, classSchedulesId);
        return ResponseEntity.noContent().build();
    }
}
