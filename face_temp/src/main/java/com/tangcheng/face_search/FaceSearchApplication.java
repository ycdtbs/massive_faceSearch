package com.tangcheng.face_search;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.EnableJms;
import springfox.documentation.oas.annotations.EnableOpenApi;

@SpringBootApplication
@EnableOpenApi
@EnableJms
@MapperScan("com.tangcheng.face_search.mapper")
public class FaceSearchApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(FaceSearchApplication.class, args);
    }
}
