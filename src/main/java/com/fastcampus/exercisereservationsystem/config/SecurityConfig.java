package com.fastcampus.exercisereservationsystem.config;

import com.fastcampus.exercisereservationsystem.common.filter.JwtAuthenticationFilter;
import com.fastcampus.exercisereservationsystem.common.filter.JwtExceptionFilter;
import com.fastcampus.exercisereservationsystem.domain.user.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
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

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
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
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/user/{userId}/classSchedules/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/user/{userId}/classSchedules")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/user/{userId}/classSchedules")
                        .hasRole("USER")
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
