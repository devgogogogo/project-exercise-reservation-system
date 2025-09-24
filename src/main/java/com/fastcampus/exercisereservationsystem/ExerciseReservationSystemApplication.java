package com.fastcampus.exercisereservationsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ExerciseReservationSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExerciseReservationSystemApplication.class, args);
    }

}
