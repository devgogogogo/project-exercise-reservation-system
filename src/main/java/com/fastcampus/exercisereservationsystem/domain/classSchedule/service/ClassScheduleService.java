package com.fastcampus.exercisereservationsystem.domain.classSchedule.service;

import com.fastcampus.exercisereservationsystem.common.exception.BizException;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.request.CreateClassScheduleRequest;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.request.UpdateClassScheduleRequest;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.response.CreateClassScheduleResponse;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.response.GetClassScheduleResponse;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.dto.response.UpdateClassScheduleResponse;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.entity.ClassScheduleEntity;
import com.fastcampus.exercisereservationsystem.domain.classSchedule.repository.ClassScheduleRepository;
import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import com.fastcampus.exercisereservationsystem.domain.user.enums.Role;
import com.fastcampus.exercisereservationsystem.domain.user.exception.UserErrorCode;
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

    private void requireAdmin(UserEntity actor) {
        if (actor == null) throw new BizException(UserErrorCode.USER_UNAUTHORIZED);
        if (actor.getRole() != Role.ADMIN) throw new BizException(UserErrorCode.USER_FORBIDDEN);
    }

    public CreateClassScheduleResponse createClassSchedule(CreateClassScheduleRequest request,UserEntity userEntity) {
        requireAdmin(userEntity);
        ClassScheduleEntity classScheduleEntity;
        classScheduleEntity = new ClassScheduleEntity(request.classname(),request.startTime(),request.endTime(),request.date(),request.capacity(),userEntity);
        classScheduleRepository.save(classScheduleEntity);
        return CreateClassScheduleResponse.from(classScheduleEntity);
    }


    public List<GetClassScheduleResponse> getByDate(UserEntity userEntity, LocalDate date) {
        requireAdmin(userEntity);
        List<ClassScheduleEntity> classScheduleEntities = classScheduleRepository.findAllByDateOrderByStartTimeAsc(date);
       return classScheduleEntities.stream().map(GetClassScheduleResponse::from).toList();
    }

    public UpdateClassScheduleResponse updateClassSchedule(UserEntity userEntity,UpdateClassScheduleRequest request,Long classSchedulesId) {
        requireAdmin(userEntity);
        ClassScheduleEntity classScheduleEntity = classScheduleRepository.findById(classSchedulesId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."));
        classScheduleEntity.updateClassSchedule(request.classname(), request.startTime(),request.endTime(),request.date(),request.capacity());
        classScheduleRepository.save(classScheduleEntity);
        return UpdateClassScheduleResponse.from(classScheduleEntity);
    }

    public void deleteClassSchedule(UserEntity userEntity, Long classSchedulesId) {
        requireAdmin(userEntity);
        ClassScheduleEntity classScheduleEntity = classScheduleRepository.findById(classSchedulesId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."));
        classScheduleRepository.delete(classScheduleEntity);
    }


}
