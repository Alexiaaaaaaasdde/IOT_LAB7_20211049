package com.example.registroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class RegistroServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RegistroServiceApplication.class, args);
    }
}
