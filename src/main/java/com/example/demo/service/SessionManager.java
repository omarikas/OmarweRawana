package com.example.demo.service;

import com.example.demo.model.usersess;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class SessionManager {

    private final RedisTemplate<String, usersess> redisTemplate;
    private static final Duration SESSION_TTL = Duration.ofHours(1);

    public SessionManager(RedisTemplate<String, usersess> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void createSession(usersess session) {
        String key = buildSessionKey(session.getToken());
        redisTemplate.opsForValue().set(key, session, SESSION_TTL);
    }

    public void terminateSession(String token) {
        redisTemplate.delete(buildSessionKey(token));
    }

    public usersess fetchSession(String token) {
        return redisTemplate.opsForValue().get(buildSessionKey(token));
    }

    public boolean isSessionValid(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        return Boolean.TRUE.equals(redisTemplate.hasKey(buildSessionKey(token)));
    }

    public Long extractUserId(String token) {
        usersess session = fetchSession(token);
        return (session != null) ? session.getUserId().getId() : null;
    }

    private String buildSessionKey(String token) {
        return "session:" + token;
    }
}
