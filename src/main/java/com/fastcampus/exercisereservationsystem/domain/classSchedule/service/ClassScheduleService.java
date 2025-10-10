package com.fastcampus.exercisereservationsystem.domain.classSchedule.service;

import com.fastcampus.exercisereservationsystem.common.exception.BizException;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.request.CreateClassScheduleRequest;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.request.UpdateClassScheduleRequest;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.response.CreateClassScheduleResponse;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.response.GetClassScheduleResponse;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.response.UpdateClassScheduleResponse;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.entity.ClassScheduleEntity;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.enums.ScheduleStatus;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.exception.ClassScheduleErrorCode;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.repository.ClassScheduleRepository;
import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassScheduleService {

    private final ClassScheduleRepository classScheduleRepository;
    @Transactional
    public CreateClassScheduleResponse createClassSchedule(CreateClassScheduleRequest request,UserEntity userEntity) {
        ClassScheduleEntity classScheduleEntity;
        classScheduleEntity = new ClassScheduleEntity(request.classname(),request.startTime(),request.endTime(),request.date(),request.capacity(),userEntity);
        classScheduleRepository.save(classScheduleEntity);
        return CreateClassScheduleResponse.from(classScheduleEntity);
    }

    @Transactional(readOnly = true)
    public List<GetClassScheduleResponse> getByDate(LocalDate date) {
        List<ClassScheduleEntity> classScheduleEntities = classScheduleRepository.findAllByDateOrderByStartTimeAsc(date);
       return classScheduleEntities.stream().map(GetClassScheduleResponse::from).toList();
    }
    @Transactional
    public UpdateClassScheduleResponse updateClassSchedule(UpdateClassScheduleRequest request,Long classSchedulesId) {
        ClassScheduleEntity classScheduleEntity = classScheduleRepository.findById(classSchedulesId).orElseThrow(() -> new BizException(ClassScheduleErrorCode.SCHEDULE_NOT_FOUND));
        classScheduleEntity.updateClassSchedule(request.classname(), request.startTime(),request.endTime(),request.date(),request.capacity());
        classScheduleRepository.save(classScheduleEntity);
        return UpdateClassScheduleResponse.from(classScheduleEntity);
    }
    @Transactional
    public void deleteClassSchedule(Long classSchedulesId) {
        ClassScheduleEntity classScheduleEntity = classScheduleRepository.findById(classSchedulesId).orElseThrow(() -> new BizException(ClassScheduleErrorCode.SCHEDULE_NOT_FOUND));
        classScheduleRepository.delete(classScheduleEntity);
    }
}
