package com.fastcampus.exercisereservationsystem.common.service;

import com.fastcampus.exercisereservationsystem.config.JwtToken;
import com.fastcampus.exercisereservationsystem.config.RedisDao;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;
    private final RedisDao redisDao;


    public JwtService(@Value("${jwt.secret-key}") String key, RedisDao redisDao) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
        this.redisDao = redisDao;
    }

    public JwtToken generateToken(String username) {

        Date now = new Date();
        Date accessExp = new Date(now.getTime() + 3600 * 1000 * 3); //3시간
        Date refreshExp = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000); //7일

        // Access Token 생성
        String accessToken = Jwts.builder()
                .setSubject(username)
                .signWith(key, SignatureAlgorithm.HS256) //HMAC-SHA256으로 서명(위조 방지)
                .setIssuedAt(now) //발행된 시각
                .setExpiration(accessExp) //만료시간
                .compact(); //최종 문자열(aaa.bbb.ccc)로 직렬화

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setSubject(username)
                .signWith(key,SignatureAlgorithm.HS256)
                .setIssuedAt(now)
                .setExpiration(refreshExp)
                .compact();

        //redis에 refreshToken 저장 유효기간 7일
        redisDao.setValues("RT:" + username, refreshToken, Duration.ofDays(7));
        return new JwtToken(accessToken, refreshToken);
    }

    //토큰에서 username(sub)추출
    public String extractUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)  //여기서 서명/만료를 함께 체크
                .getBody();
        return claims.getSubject();
    }

    // ✅ [1차 검증] 토큰 자체가 유효한지(서명/만료/형식) 검사
    //    지금 네 구현 그대로 OK. 오탈자만 고침(유요한지→유효한지, 먄료→만료)
    public boolean isTokenValid(String token) {
        try {
            //유효하지 않으면 JwtException 발생
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)  //여기서 서명/만료를 함께 체크
                    .getBody();
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false; //서명 불일치, 만료, 형식 오류 등
        }
    }

    public JwtToken reissue(String refreshToken) {
        if (!isTokenValid(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다. 다시 로그인하세요.");
        }
        //username 추출
        String username = extractUsername(refreshToken);

        //redis 에서 refreshToken 꺼내오고
        String saved = (String) redisDao.getValue("RT:" + username);
        if (saved == null || !saved.equals(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 일치하지 않습니다.");
        }

        //4) 통과시
        JwtToken newToken = generateToken(username);

        //5) // 5) Redis 갱신 (generateToken 내부에서 이미 RT:{username} 갱신/TTL 설정)
        return newToken;
    }
}
