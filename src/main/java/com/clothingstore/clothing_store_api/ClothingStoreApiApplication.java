package com.clothingstore.clothing_store_api;


import com.clothingstore.clothing_store_api.util.DotenvUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;


@SpringBootApplication
public class ClothingStoreApiApplication {

    public static void main(String[] args) {
        System.out.println("Thu muc goc: " + new File(".").getAbsolutePath());
        DotenvUtil.loadDotenv();
        SpringApplication.run(ClothingStoreApiApplication.class, args);
    }

}
