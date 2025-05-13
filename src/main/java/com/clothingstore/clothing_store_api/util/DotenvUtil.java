package com.clothingstore.clothing_store_api.util;

import io.github.cdimascio.dotenv.Dotenv;

public class DotenvUtil {
    public static void loadDotenv() {
        System.out.println("DotenvUtil: Loading .env file...");
        Dotenv dotenv = Dotenv.configure().directory("./").load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        System.out.println("DotenvUtil: DB_URL=" + System.getProperty("DB_URL"));
        System.out.println("DotenvUtil: DB_USERNAME=" + System.getProperty("DB_USERNAME"));
        System.out.println("DotenvUtil: DB_PASSWORD=" + System.getProperty("DB_PASSWORD"));
    }
}
