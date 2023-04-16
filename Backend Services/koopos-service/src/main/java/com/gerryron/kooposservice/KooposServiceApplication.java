package com.gerryron.kooposservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class KooposServiceApplication {

    public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Jakarta"));
        SpringApplication.run(KooposServiceApplication.class, args);
    }

}
