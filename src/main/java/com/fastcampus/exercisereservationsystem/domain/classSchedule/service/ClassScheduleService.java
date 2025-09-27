package com.fastcampus.exercisereservationsystem.domain.classSchedule.service;

import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.request.CreateClassScheduleRequest;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.request.UpdateClassScheduleRequest;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.response.CreateClassScheduleResponse;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.response.GetClassScheduleResponse;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.response.UpdateClassScheduleResponse;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.entity.ClassScheduleEntity;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.repository.ClassScheduleRepository;
import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import com.fastcampus.exercisereservationsystem.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassScheduleService {

    private final ClassScheduleRepository classScheduleRepository;
    private final UserRepository userRepository;

    public CreateClassScheduleResponse createClassSchedule(CreateClassScheduleRequest request,Long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다."));
        ClassScheduleEntity classScheduleEntity;
        classScheduleEntity = new ClassScheduleEntity(request.classname(),request.startTime(),request.endTime(),request.date(),request.capacity(),userEntity);
        classScheduleRepository.save(classScheduleEntity);
        return CreateClassScheduleResponse.from(classScheduleEntity);
    }


    public List<GetClassScheduleResponse> getByDate(Long userId, LocalDate date) {
        userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다."));
        List<ClassScheduleEntity> classScheduleEntities = classScheduleRepository.findAllByDateOrderByStartTimeAsc(date);
       return classScheduleEntities.stream().map(GetClassScheduleResponse::from).toList();
    }

    public UpdateClassScheduleResponse updateClassSchedule(Long userId, UpdateClassScheduleRequest request,Long classSchedulesId) {
        ClassScheduleEntity classScheduleEntity = classScheduleRepository.findById(classSchedulesId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."));
        classScheduleEntity.updateClassSchedule(request.className(), request.startTime(),request.endTime(),request.date(),request.capacity());
        classScheduleRepository.save(classScheduleEntity);
        return UpdateClassScheduleResponse.from(classScheduleEntity);
    }

    public void deleteClassSchedule(Long userId, Long classSchedulesId) {
        ClassScheduleEntity classScheduleEntity = classScheduleRepository.findById(classSchedulesId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."));
        classScheduleRepository.delete(classScheduleEntity);
    }
}
