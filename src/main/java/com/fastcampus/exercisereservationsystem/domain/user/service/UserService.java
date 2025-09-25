package com.fastcampus.exercisereservationsystem.domain.user.service;

import com.fastcampus.exercisereservationsystem.domain.user.dto.request.CreateUserRequest;
import com.fastcampus.exercisereservationsystem.domain.user.dto.request.UpdateUserRequest;
import com.fastcampus.exercisereservationsystem.domain.user.dto.response.CreateUserResponse;
import com.fastcampus.exercisereservationsystem.domain.user.dto.response.GetUserResponse;
import com.fastcampus.exercisereservationsystem.domain.user.dto.response.UpdateUserResponse;
import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import com.fastcampus.exercisereservationsystem.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    //반복 메서드
    public UserEntity getUserEntity(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("유저가 없습니다."));
    }

    //회원 생성
    public CreateUserResponse createUser(@Valid CreateUserRequest request) {

        UserEntity userEntity = UserEntity.of(
                request.name(),
                request.username(),
                bCryptPasswordEncoder.encode(request.password()),
                request.startAt(),
                request.endAt());

        UserEntity savedUserEntity = userRepository.save(userEntity);
       return CreateUserResponse.from(savedUserEntity);
    }

    //회원 전체 조회
    public List<GetUserResponse> getUserList() {
        List<UserEntity> userEntityList = userRepository.findAll();
        return userEntityList.stream().map(userEntity -> GetUserResponse.from(userEntity)).toList();
    }

    //회원 단건 조회
    public GetUserResponse getUserById(Long userId) {

        UserEntity userEntity = getUserEntity(userId);
        return GetUserResponse.from(userEntity);
    }

    //회원 기간 수정
    public UpdateUserResponse updateUserPeriod(String username, UpdateUserRequest request) {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("유저가 없습니다."));

        userEntity.updatePeriod(request.startAt(), request.endAt());
        userRepository.save(userEntity);
        return UpdateUserResponse.from(userEntity);
    }

    //회원 삭제 (추후에 소프르 delete로 바꿀예정)
    public void deleteUser(String username) {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("유저가 없습니다."));
        userRepository.delete(userEntity);
    }
}
