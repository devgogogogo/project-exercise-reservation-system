package com.fastcampus.exercisereservationsystem.domain.user.entity;

import com.fastcampus.exercisereservationsystem.domain.user.enums.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Getter
@NoArgsConstructor
@Table(name = "users")
@Entity
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false ,unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate startAt; //회원등록 시작일

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate endAt; //회원등록 종료일


    public static UserEntity of(String name, String username, String password, LocalDate startAt, LocalDate endAt) {
        UserEntity user = new UserEntity();
        user.name = name;
        user.username = username;
        user.password = password;
        user.role = Role.USER;
        user.startAt = startAt;
        user.endAt = endAt;
        return user;
    }

    public void updatePeriod(LocalDate newStartAt, LocalDate newEndAt) {
        this.startAt = newStartAt;
        this.endAt = newEndAt;
    }

    /**
     * 1) 왜 ROLE_(언더스코어 포함) 접두가 들어가?
     * 스프링 시큐리티는 “롤(role)”과 “어써리티(authority)”를 구분해.
     * hasRole("ADMIN")을 쓰면, 내부적으로 "ROLE_ADMIN" 이라는 권한 문자열(GrantedAuthority) 을 찾는다.
     * 즉, hasRole("ADMIN") == hasAuthority("ROLE_ADMIN").
     * 이 “ROLE_ 접두”는 스프링의 디폴트 규칙이야(기본 role prefix). 그래서 getAuthorities()에서 역할을 권한으로 바꿔줄 때 "ROLE_" + 역할명 으로 만들어 주는 게 표준이야.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role.equals(Role.ADMIN)) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),//hasRole("ADMIN")용(표준) <<-- 이걸 더 권장
//                    new SimpleGrantedAuthority("ADMIN"),  // hasAuthority("ADMIN") 같은 '비표준' 체크용
                    new SimpleGrantedAuthority("ROLE_USER"));//
//                    new SimpleGrantedAuthority("USER")); //
        } else {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_USER"));
//                    new SimpleGrantedAuthority(Role.USER.name()));
        }
    }

    @Override
    public boolean isAccountNonExpired() {   //계정 자체의 만료 여부. true면 “만료 안 됨(유효)”, false면 로그인 거절(예: 장기 미접속 계정 만료)
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {  //계정 잠금 여부(비정상 로그인 시도 N회, 관리자 차단 등). true면 “잠금 아님(유효)”, false면 로그인 거절.
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() { //자격 증명(비밀번호) 만료 여부. true면 “비번 유효”, false면 로그인 거절(비번 변경 유도).
        return true;
    }

    @Override
    public boolean isEnabled() { //계정 활성화 여부(이메일 미인증, 탈퇴 처리 등). true면 활성, false면 로그인 거절.
        return true;
    }
}
