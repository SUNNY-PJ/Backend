package com.sunny.backend.util;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.sunny.backend.common.CommonCustomException;
import com.sunny.backend.common.CommonErrorCode;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.notification.domain.NotificationType;
import com.sunny.backend.notification.dto.response.AlarmResponse;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisTemplate<String, Object> objectRedisTemplate;
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

    public void getHashData(String key) {
        RedisOperations<String, Object> list = objectRedisTemplate.opsForList().getOperations();
        System.out.println(list.opsForList().range(key, 0, list.opsForList().size(key)));
    }

    public void setHashData(String key, AlarmResponse alarmResponse) {
        RedisOperations<String, Object> list = objectRedisTemplate.opsForList().getOperations();
        list.opsForList().rightPush(key, alarmResponse);
    }
}
