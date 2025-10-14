package com.fastcampus.exercisereservationsystem.domain;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

    static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.36");
    static final GenericContainer<?> redis = new GenericContainer<>("redis:7.2").withExposedPorts(6379);

    // ★ 선(先)기동 — 스프링 컨텍스트가 연결 만들기 전에 반드시 컨테이너를 띄운다
    static {
        mysql.start();
        redis.start();
    }

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", mysql::getJdbcUrl);
        r.add("spring.datasource.username", mysql::getUsername);
        r.add("spring.datasource.password", mysql::getPassword);

        r.add("spring.data.redis.host", redis::getHost);
        r.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }
}
