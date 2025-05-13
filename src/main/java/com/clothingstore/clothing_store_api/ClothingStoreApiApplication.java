package com.clothingstore.clothing_store_api;

import com.clothingstore.clothing_store_api.config.EnvConfig;
import io.github.cdimascio.dotenv.Dotenv;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
//@OpenAPIDefinition(info = @Info(title = "Clothing Store API", version = "1.0", description = "API for Clothing Store"))
public class ClothingStoreApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClothingStoreApiApplication.class, args);
    }

}
