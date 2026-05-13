package com.librosphere.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.librosphere")
@EnableJpaRepositories(basePackages = "com.librosphere")
@EntityScan(basePackages = "com.librosphere")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
