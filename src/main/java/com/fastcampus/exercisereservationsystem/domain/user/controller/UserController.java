package com.fastcampus.exercisereservationsystem.domain.user.controller;

import com.fastcampus.exercisereservationsystem.domain.user.dto.request.CreateUserRequest;
import com.fastcampus.exercisereservationsystem.domain.user.dto.response.CreateUserResponse;
import com.fastcampus.exercisereservationsystem.domain.user.repository.UserRepository;
import com.fastcampus.exercisereservationsystem.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        CreateUserResponse response = userService.createUser(request);
        return ResponseEntity.ok().body(response);
    }


}
