package com.clothingstore.clothing_store_api.config;

import com.clothingstore.clothing_store_api.util.DotenvUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvConfig {
    static {
        DotenvUtil.loadDotenv();
    }
}
