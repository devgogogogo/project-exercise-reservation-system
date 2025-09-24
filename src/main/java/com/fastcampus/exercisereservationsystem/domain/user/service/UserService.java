package com.fastcampus.exercisereservationsystem.domain.user.service;

import com.fastcampus.exercisereservationsystem.domain.user.dto.request.CreateUserRequest;
import com.fastcampus.exercisereservationsystem.domain.user.dto.response.CreateUserResponse;
import com.fastcampus.exercisereservationsystem.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public CreateUserResponse createUser(@Valid CreateUserRequest request) {

        

        return null;
    }
}
