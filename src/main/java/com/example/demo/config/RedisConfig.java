package com.example.demo.config;

import com.example.demo.model.usersess;
import com.example.demo.service.SessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, usersess> sessionRedisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, usersess> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());
        template.setValueSerializer(sessionRedisSerializer());
        template.setHashValueSerializer(sessionRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public SessionManager sessionManager(RedisTemplate<String, usersess> sessionRedisTemplate) {
        SessionManager manager = new SessionManager(sessionRedisTemplate);
      
        return manager;
    }

    @Bean
    public GenericJackson2JsonRedisSerializer sessionRedisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }
}