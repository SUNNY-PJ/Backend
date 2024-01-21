package com.sunny.backend.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import com.sunny.backend.common.CommonCustomException;
import com.sunny.backend.common.CommonErrorCode;
import com.sunny.backend.common.exception.CustomException;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, String> redisTemplate;

    public String getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void isExistData(String key) {
        if(redisTemplate.opsForValue().get(key) == null) {
            throw new CommonCustomException(CommonErrorCode.TOKEN_EXPIRED);
        }
    }

    public void setValuesWithTimeout(String key, String value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MILLISECONDS);
    }

    public void deleteData(String key) {
        isExistData(key);
        redisTemplate.delete(key);
    }
}
