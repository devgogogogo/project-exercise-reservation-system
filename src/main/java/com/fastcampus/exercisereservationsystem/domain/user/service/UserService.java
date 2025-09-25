package com.fastcampus.exercisereservationsystem.domain.user.service;

import com.fastcampus.exercisereservationsystem.domain.user.dto.request.CreateUserRequest;
import com.fastcampus.exercisereservationsystem.domain.user.dto.response.CreateUserResponse;
import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import com.fastcampus.exercisereservationsystem.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public CreateUserResponse createUser(@Valid CreateUserRequest request) {

        UserEntity userEntity = UserEntity.of(
                request.name(),
                request.username(),
                bCryptPasswordEncoder.encode(request.password()),
                request.startedAt(),
                request.endedAt());

        UserEntity savedUserEntity = userRepository.save(userEntity);
       return CreateUserResponse.from(savedUserEntity);
    }
}
