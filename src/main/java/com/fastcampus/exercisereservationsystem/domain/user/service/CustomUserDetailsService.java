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

    //loadUserByUsername은 Spring Security가 로그인 과정에서 자동으로 호출하는 메서드야.
    //사용자가 로그인 폼에서 username을 입력하면 그 값이 여기로 넘어오고, DB에서 조회해서 UserDetails를 반환하는 거지.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        return userEntity;
    }
}
