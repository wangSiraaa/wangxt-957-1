package com.hospital.accompany;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hospital.accompany.mapper")
public class AccompanyApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccompanyApplication.class, args);
    }
}
