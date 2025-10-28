package com.fastcampus.exercisereservationsystem.common.filter;

import com.fastcampus.exercisereservationsystem.common.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1) 헤더에서 우선 토큰 추출
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = null;
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            token = header.substring(BEARER_PREFIX.length()).trim();
        }

        // 2) 헤더에 없으면 쿠키(ACCESS_TOKEN)에서 보조적으로 추출
        if (token == null || token.isBlank()) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if ("ACCESS_TOKEN".equals(c.getName())) {
                        token = c.getValue();
                        break;
                    }
                }
            }
        }

        // 3) 토큰이 여전히 없으면 인증 시도 없이 다음 필터로
        if (token == null || token.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 4) 이미 인증되어 있으면 중복 세팅 방지
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 5) 토큰 유효성 검사
            if (!jwtService.isTokenValid(token)) {
                // 유효하지 않으면 인증 세팅 없이 패스 → 최종 접근 제어는 Security가 처리(403/리다이렉트)
                filterChain.doFilter(request, response);
                return;
            }

            // 6) 사용자 로드 및 SecurityContext 설정
            String username = jwtService.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);

        } catch (Exception ignored) {
            // 파싱/검증 중 예외가 나도 인증 없이 통과시켜서, Security 규칙에 따라 처리되게 함
        }

        filterChain.doFilter(request, response);
    }
}
