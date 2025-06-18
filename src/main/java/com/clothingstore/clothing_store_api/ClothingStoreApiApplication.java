package com.clothingstore.clothing_store_api;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ClothingStoreApiApplication {

    public static void main(String[] args) {
//        System.out.println("Thu muc goc: " + new File(".").getAbsolutePath());
        DotenvUtil.loadDotenv();
        SpringApplication.run(ClothingStoreApiApplication.class, args);
    }

}
