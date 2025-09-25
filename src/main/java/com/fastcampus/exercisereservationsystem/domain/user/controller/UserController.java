package com.fastcampus.exercisereservationsystem.domain.user.controller;

import com.fastcampus.exercisereservationsystem.domain.user.dto.request.CreateUserRequest;
import com.fastcampus.exercisereservationsystem.domain.user.dto.request.UpdateUserRequest;
import com.fastcampus.exercisereservationsystem.domain.user.dto.response.CreateUserResponse;
import com.fastcampus.exercisereservationsystem.domain.user.dto.response.GetUserResponse;
import com.fastcampus.exercisereservationsystem.domain.user.dto.response.UpdateUserResponse;
import com.fastcampus.exercisereservationsystem.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //회원생성
    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody CreateUserRequest request) {
        CreateUserResponse response = userService.createUser(request);
        return ResponseEntity.ok().body(response);
    }

    //회원 전체 조회
    @GetMapping
    public ResponseEntity<List<GetUserResponse>> getUserList() {
        List<GetUserResponse> list = userService.getUserList();
        return ResponseEntity.ok().body(list);
    }

    //회원 단권 조회
    @GetMapping("/{userId}")
    public ResponseEntity<GetUserResponse> getUserById(@PathVariable Long userId) {
        GetUserResponse response = userService.getUserById(userId);
        return ResponseEntity.ok().body(response);
    }

    // 회원 정보중 연장으로 인한 기간 수정
    @PutMapping("/{username}")
    public ResponseEntity<UpdateUserResponse> updateUserPeriod(@PathVariable String username, @RequestBody UpdateUserRequest request) {
        UpdateUserResponse response = userService.updateUserPeriod(username, request);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }
}
