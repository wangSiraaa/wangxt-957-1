package com.hospital.accompany;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.hospital.accompany.mapper")
@EnableScheduling
public class AccompanyApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccompanyApplication.class, args);
    }
}
