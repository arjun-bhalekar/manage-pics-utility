package com.manage.pic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Scanner;

@SpringBootApplication
@EnableScheduling
public class ManagePicsUtilityApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManagePicsUtilityApplication.class, args);
    }

}
