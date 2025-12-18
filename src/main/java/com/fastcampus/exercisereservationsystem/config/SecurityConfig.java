package com.fastcampus.exercisereservationsystem.config;

import com.fastcampus.exercisereservationsystem.common.filter.JwtAuthenticationFilter;
import com.fastcampus.exercisereservationsystem.common.filter.JwtExceptionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile; // test 프로파일 분리용
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Profile("!test") // test 프로파일에서는 이 SecurityConfig 자체가 로딩되지 않게 분리
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter; // JWT 인증 담당 필터
    private final JwtExceptionFilter jwtExceptionFilter; // JWT 관련 예외를 401 JSON으로 변환하는 필터

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() { // 비밀번호 해싱용 인코더
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager(); // 스프링이 구성한 AuthenticationManager 그대로 사용
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() { // 프론트엔드 연동을 위한 CORS 설정
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth

                                /* ===================== ↓↓↓ 여기부터는 “그대로 유지” ↓↓↓ ===================== */

                                /* ----- 정적/공용 뷰 ----- */
                                .requestMatchers(HttpMethod.GET,
                                        "/", "/login", "/signup",
                                        "/reservation",
                                        "/notices", "/notices/*", "/notices/new",
                                        "/css/**", "/js/**", "/images/**").permitAll()

                                /* ----- 내 정보 페이지(뷰) ----- */
                                .requestMatchers(HttpMethod.GET, "/my-info").authenticated()

                                /* ----- 유저 API 허용(본인만) ----- */
                                // ↓↓↓ 이 두 줄은 반드시 "/api/users/**" ADMIN 규칙보다 위에 두세요!
                                .requestMatchers(HttpMethod.GET, "/api/users/person").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/users/period").authenticated()

                                /* ----- 나머지 유저 API ----- */
                                .requestMatchers(HttpMethod.POST, "/api/users/login", "/api/users/signup", "/api/users/refresh").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()
                                .requestMatchers("/api/users/**").hasRole("ADMIN")

                                /* ----- 공지 API ----- */
                                .requestMatchers(HttpMethod.GET, "/api/notices", "/api/notices/search", "/api/notices/*").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/notices").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/notices/*").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/notices/*").hasRole("ADMIN")

                                /* ----- 수업 스케줄 API ----- */
                                .requestMatchers(HttpMethod.GET, "/api/classSchedules").hasAnyRole("ADMIN","USER")
                                .requestMatchers(HttpMethod.GET, "/api/classSchedules/**").hasAnyRole("ADMIN","USER")
                                .requestMatchers(HttpMethod.POST, "/api/classSchedules/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT,  "/api/classSchedules/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE,"/api/classSchedules/**").hasRole("ADMIN")

                                /* ----- 페이지(예시) ----- */
                                .requestMatchers(HttpMethod.GET,"/classSchedule-calendar", "/classSchedule-createForm").permitAll()
                                .requestMatchers("/classSchedule-updateForm").hasRole("ADMIN")
                                .requestMatchers("/classSchedule-list").permitAll()

                                /* ----- 기타 ----- */
                                .anyRequest().authenticated()

                        /* ===================== ↑↑↑ 여기까지 그대로 유지 ↑↑↑ ===================== */
                )

                .cors(Customizer.withDefaults()) // 위에서 정의한 CORS 설정 적용
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT 기반이라 세션 미사용
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**")) // REST API는 CSRF 비활성화

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                ) // JWT 인증 필터를 기본 UsernamePasswordAuthenticationFilter보다 앞에 배치

                .addFilterBefore(
                        jwtExceptionFilter,
                        JwtAuthenticationFilter.class
                ) // 예외 필터가 JWT 인증 필터 전체를 감싸도록 순서 고정 (핵심)

                .formLogin(AbstractHttpConfigurer::disable) // form-login 미사용
                .httpBasic(AbstractHttpConfigurer::disable); // basic auth 미사용

        return http.build();
    }
}
