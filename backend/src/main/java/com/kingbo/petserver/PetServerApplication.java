package com.kingbo.petserver;

import com.kingbo.myjwtutilsstarter.MyJwtUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@MapperScan(basePackages = "com.kingbo.petserver.dao")
//@ComponentScan(basePackages = {"com.kingbo.myjwtutilsstarter","com.kingbo.petserver"})
@Import(MyJwtUtils.class)
public class PetServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PetServerApplication.class, args);
    }
}