package com.gerryron.koopos.grocerystoreservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import java.util.TimeZone;

@SpringBootApplication
@EnableEurekaClient
public class GroceryStoreServiceApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Jakarta"));
        SpringApplication.run(GroceryStoreServiceApplication.class, args);
    }

}
