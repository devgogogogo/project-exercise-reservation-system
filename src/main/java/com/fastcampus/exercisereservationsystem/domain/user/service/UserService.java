package com.fastcampus.exercisereservationsystem.domain.user.service;

import com.fastcampus.exercisereservationsystem.common.exception.BizException;
import com.fastcampus.exercisereservationsystem.common.service.JwtService;
import com.fastcampus.exercisereservationsystem.config.JwtToken;
import com.fastcampus.exercisereservationsystem.domain.user.dto.request.CreateUserRequest;
import com.fastcampus.exercisereservationsystem.domain.user.dto.request.LoginUserRequest;
import com.fastcampus.exercisereservationsystem.domain.user.dto.request.UpdateUserRequest;
import com.fastcampus.exercisereservationsystem.domain.user.dto.response.*;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final Clock clock = Clock.system(ZoneId.of("Asia/Seoul"));
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

    //내정보 조회
    public GetUserResponse getUserInfo(UserEntity userEntity) {

        return GetUserResponse.from(userEntity);
    }
    //회원 기간 조회
    @Transactional(readOnly = true)
    public GetUserPeriodResponse getUserPeriod(UserEntity userEntity) {

        LocalDate end = userEntity.getEndAt();
        LocalDate start = userEntity.getStartAt();

        if (end == null) {
            // 만료일 정보가 없으면 0으로 간주 (정책에 따라 null 반환도 가능)
            return GetUserPeriodResponse.from(0L);
        }
        // 잘못된 데이터 방지 (end < start)
        if (start != null && end.isBefore(start)) {
            return GetUserPeriodResponse.from(0L);
        }

        /**
         * // 예시 --> ChronoUnit.DAYS.between(10월 29일, 10월 30일) // 결과: 1 그래서 +1을 해준다.
         * 👉 이건 “29일부터 30일까지 1일 간격”이라는 뜻이에요.
         * 즉, 오늘(29일)을 포함하지 않습니다.
         *
         * 그래서 +1을 해줘야 실제 “남은 일수”가 오늘 포함 2일 남음(D-2) 으로 계산돼요.
         */
        LocalDate today = LocalDate.now(clock);
        long remaining;
        //“왼쪽 날짜가 오른쪽 날짜 보다 이후면 기간끝이므로 0
        if (today.isAfter(end)) {
            remaining = 0L;
        } else if (start != null && end.isBefore(today)) {
            // 아직 시작 전 → 전체 이용 가능 일수(시작~만료, 양끝 포함)
            remaining = ChronoUnit.DAYS.between(start, end) + 1;
        } else {
            // 진행 중 → 오늘 포함 남은 일수(오늘~만료, 양끝 포함)
            remaining = ChronoUnit.DAYS.between(today, end) + 1;
        }
        if (remaining < 0) {
            // 최종 안전장치
            remaining = 0;
        }

        return GetUserPeriodResponse.from(remaining);
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
