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
        )

        // CORS (아래 corsConfigurationSource())
                .cors(Customizer.withDefaults())
                // 세션 X
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // CSRF: API는 JWT stateless 이므로 전체 제외 (또는 "/api/**"만 제외)

                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))

                // 필터 순서: 예외 먼저, 인증 다음
                .addFilterBefore(jwtExceptionFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // 폼/베이직 인증 비활성화 (JSON 로그인)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

}
