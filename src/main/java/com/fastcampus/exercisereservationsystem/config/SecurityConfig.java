package com.fastcampus.exercisereservationsystem.config;

import com.fastcampus.exercisereservationsystem.common.filter.JwtAuthenticationFilter;
import com.fastcampus.exercisereservationsystem.common.filter.JwtExceptionFilter;
import com.fastcampus.exercisereservationsystem.domain.user.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter; // (필드 주입 대상) 보안 필터 2종 + 사용자 조회 서비스
    private final JwtExceptionFilter jwtExceptionFilter; //JWT 관련 예외(만료/서명오류 등)를 잡아 표준화된 401 응답(JSON 등)으로 리턴하는 예외 처리 전용 필터.
    private final  CustomUserDetailsService userDetailsService; // username으로 DB에서 유저를 찾는 서비스. 스프링 시큐리티의 아이디/비번 인증 과정에서 호출됨.
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder encoder) { //"아이디/비번 인증"을 실제로 수행하는 구현체(Provider).
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(); //DaoAuthenticationProvider = DB/DAO 기반 사용자 조회 + 비밀번호 대조 담당.
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(encoder);
        return provider;
    }
    /**
     * // -> "아이디/비번 인증"을 실제로 수행하는 구현체(Provider).
     * //    DaoAuthenticationProvider = DB/DAO 기반 사용자 조회 + 비밀번호 대조 담당.
     * //    1) userDetailsService로 유저 로드
     * //    2) passwordEncoder로 비밀번호 매칭
     * //    3) 성공하면 Authentication 리턴, 실패하면 AuthenticationException 던짐.
     * //    이 Provider를 Security에 등록해두면 AuthenticationManager가 로그인 시 이걸 사용.
     */

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, AuthenticationProvider provider) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(provider)
                .build();
    }
    /**
     *  -> "인증 총괄 매니저". 인증 시도(authenticate(...))가 오면
     *     내부에 등록된 Provider(여기선 DaoAuthenticationProvider)들에게 순차 위임해 처리.
     *     HttpSecurity가 가진 공유 빌더(AuthenticationManagerBuilder)를 꺼내서
     *     우리가 만든 Provider를 끼우고 build()로 매니저를 만듦.
     *     이 Bean은 보통 로그인 서비스(UserService)에서 주입 받아
     *     authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password))
     *     이렇게 호출해서 실제 인증을 수행하고, 성공 시 JWT를 발급하는 흐름으로 이어짐.
     */


    /**
     *Bean이 필요한경우
     * 프론트가 다른 오리진(예: http://localhost:3000)에서 브라우저로 API 호출할 때
     * 브라우저 환경에서 Authorization 헤더 사용(= JWT)하거나, 쿠키로 리프레시 토큰을 주고받을 때
     *
     * 굳이 없어도 되는 경우
     * Postman, cURL 같은 서버-서버/도구 호출 (브라우저의 CORS 정책이 적용되지 않음)
     * 같은 오리진에서만 호출
     */

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/v1/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //todo : 이부분은 다시 수정해야할 부분, 이런저런 기능이 있다는걸 기억하기 위해 써 놓은거
        http
                .authorizeHttpRequests(request -> request
                        //공지사항 조회는 관리자, 유저 둘다
                        .requestMatchers(HttpMethod.GET,"/api/user/*/class-schedules/**","/api/notices").hasAnyRole("ADMIN","USER")
                        //공지사항 --> 관리자
                        .requestMatchers("/api/user/*/class-schedules/**","/api/notices").hasRole("ADMIN")

                        // 댓글 --> 유저 ,관리자
                        .requestMatchers("/api/notices/*/comments/**").hasAnyRole("ADMIN","USER")
                        //예약 --> 유져
                        .requestMatchers("/api/classSchedules/reservation").hasRole("USER")
                        //회원가입, 로그인 --> 모두 허용
                        .requestMatchers(HttpMethod.POST, "/api/users/login","/api/users")
                        .permitAll()
                        .anyRequest().authenticated()
                );
        http.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.csrf(csrf -> csrf.disable());
        http.addFilterBefore(jwtExceptionFilter,jwtAuthenticationFilter.getClass());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
