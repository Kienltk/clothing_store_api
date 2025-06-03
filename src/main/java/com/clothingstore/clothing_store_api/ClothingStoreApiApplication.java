package com.clothingstore.clothing_store_api;


import com.clothingstore.clothing_store_api.config.LoadEnv;
import com.clothingstore.clothing_store_api.util.DotenvUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ClothingStoreApiApplication {

    public static void main(String[] args) {
        DotenvUtil.loadDotenv();
        SpringApplication.run(ClothingStoreApiApplication.class, args);
    }

}
