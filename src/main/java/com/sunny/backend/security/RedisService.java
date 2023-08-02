package com.sunny.backend.security;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {
	private final RedisTemplate<String, String> redisTemplate;

	public void setValuesWithTimeout(String key, String value, long timeout) {
		redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MILLISECONDS);
	}

	public String getValues(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	public void deleteValues(String key) {
		redisTemplate.delete(key);
	}
}
