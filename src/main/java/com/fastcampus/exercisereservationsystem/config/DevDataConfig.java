package com.fastcampus.exercisereservationsystem.config;

import com.fastcampus.exercisereservationsystem.domain.user.entity.UserEntity;
import com.fastcampus.exercisereservationsystem.domain.user.enums.Role;
import com.fastcampus.exercisereservationsystem.domain.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DevDataConfig {

    @Bean
    CommandLineRunner seedAdmin(UserRepository users, PasswordEncoder encoder) {
        return args -> {
            String adminUsername = "admin@email.com";
            users.findByUsername(adminUsername).ifPresentOrElse(
                    u -> {}, // 이미 있으면 아무 것도 안함
                    () -> {
                        UserEntity admin = new UserEntity(
                                /* 생성자 형태에 맞추세요. 없다면 setter 사용 */
                        );
                        // 필드 세팅 (UserEntity 구조에 맞게 수정)
                        admin.setUsername(adminUsername);
                        admin.setPassword(encoder.encode("1234"));
                        admin.setRole(Role.ADMIN);
                        admin.setName("관리자");
                        admin.setStartAt(LocalDate.now());
                        admin.setEndAt(LocalDate.now().plusYears(1));
                        users.save(admin);
                    }
            );
        };
    }
}
