package com.fastcampus.exercisereservationsystem.config;

import com.fastcampus.exercisereservationsystem.common.filter.JwtAuthenticationFilter;
import com.fastcampus.exercisereservationsystem.common.filter.JwtExceptionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter; // (필드 주입 대상) 보안 필터 2종 + 사용자 조회 서비스
    private final JwtExceptionFilter jwtExceptionFilter; //JWT 관련 예외(만료/서명오류 등)를 잡아 표준화된 401 응답(JSON 등)으로 리턴하는 예외 처리 전용 필터.


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean // 스프링 컨테이너에 AuthenticationManager(인증 총괄) 빈으로 등록. 어디서든 주입 받아 사용.
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        // AuthenticationConfiguration: 스프링이 내부 설정(등록된 Provider, UserDetailsService, PasswordEncoder 등)로
        // 미리 만들어 둔 AuthenticationManager를 꺼내주는 헬퍼.
        // 직접 빌더로 새로 만들지 않고, 스프링이 구성한 공용 매니저를 그대로 반환(최신 권장 방식, 6.x).
        AuthenticationManager authenticationManager = config.getAuthenticationManager();
        // 반환되는 매니저는 DaoAuthenticationProvider 등을 포함하고 있어
        // authenticate(new UsernamePasswordAuthenticationToken(username, password)) 호출 시
        // DB 조회 + 비밀번호 매칭 + 계정상태 검사까지 수행함.
        return authenticationManager;
    }

    /**
     * Bean이 필요한경우
     * 프론트가 다른 오리진(예: http://localhost:3000)에서 브라우저로 API 호출할 때
     * 브라우저 환경에서 Authorization 헤더 사용(= JWT)하거나, 쿠키로 리프레시 토큰을 주고받을 때
     * <p>
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
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //이부분은 다시 수정해야할 부분, 이런저런 기능이 있다는걸 기억하기 위해 써 놓은거
        http
                .authorizeHttpRequests(request -> request
                                //수업스케쥴
                                .requestMatchers(HttpMethod.GET, "/api/classSchedules/**").hasAnyRole("ADMIN", "USER")
                                .requestMatchers("/api/classSchedules/**").hasRole("ADMIN")

                                // 댓글 --> 유저 ,관리자
                                .requestMatchers("/api/notices/*/comments/**").hasAnyRole("ADMIN", "USER")

                                //공지사항
                                .requestMatchers(HttpMethod.GET, "/api/notices/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/api/notices/**").hasRole("ADMIN")

                                //예약
                                .requestMatchers(HttpMethod.GET, "/api/class-schedules/*/reservation/**").hasRole("USER")

                                //프로그램
                                .requestMatchers(HttpMethod.GET, "/api/program", "/api/program/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/api/program", "/api/program/**").hasRole("ADMIN")


                                //유저
                                .requestMatchers(HttpMethod.POST, "/api/users/login", "/api/users/signup", "/api/users/refresh").permitAll()
                                .requestMatchers("/api/users/**").hasRole("ADMIN")
                                .requestMatchers("/api/test/**", "/swagger-ui/**", "/v3/api-docs/**", "/actuator/health").permitAll()

                                //또는 더 일반적으로 홈, 정적 리소스(css/js/img)까지 열어줘야 함.
                                .anyRequest().authenticated()
                        //테스트 용
                );

        http.cors(Customizer.withDefaults());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.csrf(csrf -> csrf.disable());
        http.addFilterBefore(jwtExceptionFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
