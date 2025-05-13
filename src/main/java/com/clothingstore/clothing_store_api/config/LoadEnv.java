package com.clothingstore.clothing_store_api.config;

import com.clothingstore.clothing_store_api.util.DotenvUtil;

public class LoadEnv {
    public static void main(String[] args) {
        DotenvUtil.loadDotenv();
    }
}
