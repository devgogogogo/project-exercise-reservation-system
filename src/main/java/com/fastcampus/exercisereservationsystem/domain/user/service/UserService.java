package com.fastcampus.exercisereservationsystem.domain.user.service;

import com.fastcampus.exercisereservationsystem.common.exception.BizException;
import com.fastcampus.exercisereservationsystem.common.service.JwtService;
import com.fastcampus.exercisereservationsystem.config.JwtToken;
import com.fastcampus.exercisereservationsystem.domain.user.dto.request.CreateUserRequest;
import com.fastcampus.exercisereservationsystem.domain.user.dto.request.LoginUserRequest;
import com.fastcampus.exercisereservationsystem.domain.user.dto.request.UpdateUserRequest;
import com.fastcampus.exercisereservationsystem.domain.user.dto.response.CreateUserResponse;
import com.fastcampus.exercisereservationsystem.domain.user.dto.response.GetUserResponse;
import com.fastcampus.exercisereservationsystem.domain.user.dto.response.LoginUserResponse;
import com.fastcampus.exercisereservationsystem.domain.user.dto.response.UpdateUserResponse;
import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import com.fastcampus.exercisereservationsystem.domain.user.exception.UserErrorCode;
import com.fastcampus.exercisereservationsystem.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    //반복 메서드
    public UserEntity getUserEntity(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new BizException(UserErrorCode.USER_NOT_FOUND));
    }

    //회원 가입
    public CreateUserResponse signup(@Valid CreateUserRequest form) {
        boolean isUser = userRepository.existsByUsername(form.username());
        boolean isNickname = userRepository.existsByNickname(form.nickname());
        //아이디 중복
        if (isUser) {
            throw new BizException(UserErrorCode.USER_ALREADY_EXISTED);
        }
        //닉네임 중복
        if (isNickname) {
            throw new BizException(UserErrorCode.USER_NICKNAME_ALREADY_EXISTED);
        }

        // 날짜 범위 검증
        if (form.startAt() != null && form.endAt() != null
                && form.startAt().isAfter(form.endAt())) {
            throw new BizException(UserErrorCode.USER_INVALID_DATE_RANGE);
        }

        UserEntity userEntity = UserEntity.of(
                form.name(),
                form.nickname(),
                form.username(),
                bCryptPasswordEncoder.encode(form.password()),
                form.startAt(),
                form.endAt());

        UserEntity savedUserEntity = userRepository.save(userEntity);
       return CreateUserResponse.from(savedUserEntity);
    }

    public LoginUserResponse login(@Valid LoginUserRequest request) {
        // 1) 스프링 시큐리티로 아이디 비밀번호 인증 시도
        Authentication authentication;
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.username(), request.password());
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException exception) {
            //아이디/ 비번이 틀렸을때 401 반환
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        // 2) 인증성공 - > 주체(principal) 꺼내기
        UserDetails principal = (UserDetails) authentication.getPrincipal();

        // 3) JWT 발급 (JwtService의 시그니처에 맞춰 호출)
        JwtToken jwtToken = jwtService.generateToken(principal.getUsername());

        return new LoginUserResponse(jwtToken.accessToken());
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
    public UpdateUserResponse updateUserPeriod(Long userId, UpdateUserRequest request) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new BizException(UserErrorCode.USER_NOT_FOUND));
        userEntity.updatePeriod(request.startAt(), request.endAt());
        userRepository.save(userEntity);
        return UpdateUserResponse.from(userEntity);
    }

    //회원 삭제 (추후에 소프르 delete로 바꿀예정)
    public void deleteUser(String username) {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(() -> new BizException(UserErrorCode.USER_NOT_FOUND));
        userRepository.delete(userEntity);
    }
}
