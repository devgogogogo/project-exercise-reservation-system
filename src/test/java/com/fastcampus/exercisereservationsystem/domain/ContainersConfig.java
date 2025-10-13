package com.fastcampus.exercisereservationsystem.domain;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {


    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

    @ServiceConnection // spring.data.redis.* 자동 주입
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.2").withExposedPorts(6379);
}
