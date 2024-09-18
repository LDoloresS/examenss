package com.codigo.examen_ss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ExamenSsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExamenSsApplication.class, args);
    }

}
