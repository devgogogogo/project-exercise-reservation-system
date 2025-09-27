package com.fastcampus.exercisereservationsystem.domain.classSchedule.controller;

import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.request.CreateClassScheduleRequest;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.request.UpdateClassScheduleRequest;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.response.CreateClassScheduleResponse;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.response.GetClassScheduleResponse;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.response.UpdateClassScheduleResponse;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.service.ClassScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api//user/{userId}/class-schedules")
@RequiredArgsConstructor
public class ClassScheduleController {

    private final ClassScheduleService classScheduleService;

    @PostMapping()
    public ResponseEntity<CreateClassScheduleResponse> createClassSchedule(
            @PathVariable Long userId,
            @RequestBody CreateClassScheduleRequest request) {
        CreateClassScheduleResponse response = classScheduleService.createClassSchedule(request, userId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<List<GetClassScheduleResponse>> getByDate(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<GetClassScheduleResponse> responses = classScheduleService.getByDate(userId, date);
        return ResponseEntity.ok().body(responses);
    }

    @PutMapping("/{classSchedulesId}")
    public ResponseEntity<UpdateClassScheduleResponse> updateClassSchedule(
            @PathVariable Long userId,
            @PathVariable Long classSchedulesId,
            @RequestBody UpdateClassScheduleRequest request) {
        UpdateClassScheduleResponse response = classScheduleService.updateClassSchedule(userId, request, classSchedulesId);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{classSchedulesId}")
    public ResponseEntity<Void> deleteClassSchedule(@PathVariable Long userId, @PathVariable Long classSchedulesId) {
        classScheduleService.deleteClassSchedule(userId, classSchedulesId);
        return ResponseEntity.noContent().build();
    }
}
