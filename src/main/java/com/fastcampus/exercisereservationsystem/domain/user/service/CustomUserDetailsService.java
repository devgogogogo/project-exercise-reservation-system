package com.fastcampus.exercisereservationsystem.domain.user.service;

import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import com.fastcampus.exercisereservationsystem.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String u;
        if (username == null) {
            u = "";                 // 널이면 빈 문자열로 대체
        } else {
            u = username.trim();    // 널이 아니면 양끝 공백 제거
        }
        if (u.isEmpty()) throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");

        UserEntity user = userRepository.findByUsername(u)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 필요 시 계정 상태 검사 (아래 3번 참고)
        return user; // UserEntity가 UserDetails면 그대로 반환
    }
}
