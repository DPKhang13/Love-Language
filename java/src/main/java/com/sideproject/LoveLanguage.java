package com.sideproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LoveLanguage {
    public static void main(String[] args) {
        SpringApplication.run(LoveLanguage.class, args);
    }
}