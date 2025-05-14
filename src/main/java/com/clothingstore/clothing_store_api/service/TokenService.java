package com.clothingstore.clothing_store_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String ACCESS_TOKEN_KEY = "access_token:";
    private static final String REFRESH_TOKEN_KEY = "refresh_token:";

    public void storeAccessToken(String userId, String accessToken) {
        redisTemplate.opsForValue().set(ACCESS_TOKEN_KEY + userId, accessToken);
    }

    public void storeRefreshToken(String userId, String refreshToken) {
        redisTemplate.opsForValue().set(REFRESH_TOKEN_KEY + userId, refreshToken);
    }

    public boolean isTokenExist(String userId, String tokenType) {
        String key = (tokenType.equals("access")) ? ACCESS_TOKEN_KEY + userId : REFRESH_TOKEN_KEY + userId;
        return redisTemplate.hasKey(key);
    }

    public void deleteToken(String userId, String tokenType) {
        String key = (tokenType.equals("access")) ? ACCESS_TOKEN_KEY + userId : REFRESH_TOKEN_KEY + userId;
        redisTemplate.delete(key);
    }
    public void blacklistRefreshToken(String refreshToken) {
        redisTemplate.opsForValue().set("blacklist:" + refreshToken, "invalid", 30, TimeUnit.DAYS);
    }

    public boolean isTokenBlacklisted(String refreshToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + refreshToken));
    }
}
