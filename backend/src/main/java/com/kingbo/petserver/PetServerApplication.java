package com.kingbo.petserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.kingbo.petserver.dao")
public class PetServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PetServerApplication.class, args);
    }
}