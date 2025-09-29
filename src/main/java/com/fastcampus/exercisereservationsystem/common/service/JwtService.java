package com.fastcampus.exercisereservationsystem.common.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;

    public JwtService(@Value("${jwt.secret-key}") String key) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + 3600 * 1000 * 3); //3시간

        String token = Jwts.builder()
                .setSubject(username)
                .signWith(key, SignatureAlgorithm.HS256) //HMAC-SHA256으로 서명(위조 방지)
                .setIssuedAt(now) //발행된 시각
                .setExpiration(exp) //만료시간
                .compact(); //최종 문자열(aaa.bbb.ccc)로 직렬화
        return token;
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


    //토큰을 파싱해서 Claims 반환(서명/만료 검증 포함)

    //토큰이 유요한지(서명/먄료 등) 간단 검증
    public boolean isTokenValid(String token) {
        try {
            //유효하지 않으면 JwtException 발생
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)  //여기서 서명/만료를 함께 체크
                    .getBody();
            return true;
        }catch (JwtException | IllegalArgumentException e) {}
        return false; //서명 불일치, 만료, 형식 오류 등

    }


}
