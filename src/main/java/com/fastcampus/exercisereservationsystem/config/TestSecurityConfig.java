package com.fastcampus.exercisereservationsystem.config;

import com.fastcampus.exercisereservationsystem.common.filter.JwtAuthenticationFilter;
import com.fastcampus.exercisereservationsystem.common.filter.JwtExceptionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Profile("test") // test 프로파일에서만 활성화
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class TestSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter; // 운영과 동일한 필터 재사용
    private final JwtExceptionFilter jwtExceptionFilter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() { // test 환경에서도 CORS 누락 방지
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
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.GET, "/actuator/health", "/actuator/info").permitAll() // 헬스체크는 인증 없이 허용
                        .requestMatchers(HttpMethod.POST, "/api/classSchedules/**/reservation").permitAll() // 부하테스트용: 예약 API는 JWT 없이 허용
                        .anyRequest().authenticated() // 그 외 요청은 인증 필요
                )

                .cors(Customizer.withDefaults()) // CORS 적용
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 미사용
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**")) // API CSRF 비활성화

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // JWT 인증 필터

                .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class); // 예외 필터가 인증 필터를 감싸도록 순서 고정

        return http.build();
    }
}
