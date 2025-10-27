package com.fastcampus.exercisereservationsystem.domain.user.controller;

import com.fastcampus.exercisereservationsystem.common.service.JwtService;
import com.fastcampus.exercisereservationsystem.config.JwtToken;
import com.fastcampus.exercisereservationsystem.domain.user.dto.request.CreateUserRequest;
import com.fastcampus.exercisereservationsystem.domain.user.dto.request.LoginUserRequest;
import com.fastcampus.exercisereservationsystem.domain.user.dto.request.UpdateUserRequest;
import com.fastcampus.exercisereservationsystem.domain.user.dto.response.*;
import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import com.fastcampus.exercisereservationsystem.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<CreateUserResponse> signup(@Valid @RequestBody CreateUserRequest request) {
        CreateUserResponse response = userService.signup(request);
        return ResponseEntity.ok().body(response);
    }

    //로그인(최초 발급)
    @PostMapping("/login")
    public ResponseEntity<LoginUserResponse> login(@Valid @RequestBody LoginUserRequest request) {
        LoginUserResponse response = userService.login(request);
        return ResponseEntity.ok().body(response);
    }
    // 로그인된 본인 정보 조회
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyInfo(
            @AuthenticationPrincipal UserEntity userEntity
    ) {
        if (userEntity == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserProfileResponse response = UserProfileResponse.from(userEntity);
        return ResponseEntity.ok(response);
    }

    // 재발급 (access 만료 + refresh 유효 시)
    @PostMapping("/refresh")
    public ResponseEntity<LoginUserResponse> refresh(
            @CookieValue(value = "refreshToken", required = false) String refreshTokenCookie,
            @RequestHeader(value = "X-Refresh-Token", required = false) String refreshTokenHeader) {

        String refreshToken = null;

        if (refreshTokenCookie != null && !refreshTokenCookie.isBlank()) {
            refreshToken = refreshTokenCookie;
        } else if (refreshTokenHeader != null && !refreshTokenHeader.isBlank()) {
            refreshToken = refreshTokenHeader;
        }
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 없습니다.");
        }
        JwtToken tokens = jwtService.reissue(refreshToken);
        return ResponseEntity.ok(new LoginUserResponse(tokens.accessToken()));
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
    @PutMapping("/{userId}")
    public ResponseEntity<UpdateUserResponse> updateUserPeriod(@PathVariable Long userId, @RequestBody UpdateUserRequest request) {
        UpdateUserResponse response = userService.updateUserPeriod(userId, request);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }
}
