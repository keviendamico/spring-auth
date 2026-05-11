package com.kevien.damico.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class BlackListService {

    private final StringRedisTemplate redisTemplate;
    private final Duration accessTokenTtl;
    private final Duration refreshTokenTtl;

    public BlackListService(StringRedisTemplate redisTemplate,
                            @Value("${jwt.accessExpirationMs}") long accessExpirationMs,
                            @Value("${jwt.refreshExpirationMs}") long refreshExpirationMs) {
        this.redisTemplate = redisTemplate;
        this.accessTokenTtl = Duration.ofMillis(accessExpirationMs);
        this.refreshTokenTtl = Duration.ofMillis(refreshExpirationMs);
    }

    public void blackListAccessToken(String jwt) {
        redisTemplate.opsForValue().set(jwt, "1", accessTokenTtl);
    }

    public void blackListRefreshToken(String jwt) {
        redisTemplate.opsForValue().set(jwt, "1", refreshTokenTtl);
    }

    public boolean isBlacklisted(String jwt) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(jwt));
    }
}