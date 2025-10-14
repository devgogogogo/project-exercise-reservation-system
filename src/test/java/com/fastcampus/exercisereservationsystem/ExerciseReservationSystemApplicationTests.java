package com.fastcampus.exercisereservationsystem;

import com.fastcampus.exercisereservationsystem.domain.ContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(ContainersConfig.class)
class ExerciseReservationSystemApplicationTests {

    @Test
    void contextLoads() {
    }

}
