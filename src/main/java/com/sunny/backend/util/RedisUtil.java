package com.sunny.backend.util;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.sunny.backend.common.CommonErrorCode;
import com.sunny.backend.common.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisUtil {
	private final RedisTemplate<String, String> redisTemplate;

	public String getData(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	public void isExistData(String key) {
		if (redisTemplate.opsForValue().get(key) == null) {
			throw new CustomException(CommonErrorCode.TOKEN_EXPIRED);
		}
	}

	public void setValuesWithTimeout(String key, String value, long timeout) {
		redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MILLISECONDS);
	}

	public String getRefreshToken(String refreshToken) {
		return redisTemplate.opsForValue().get(refreshToken);
	}

	public void deleteData(String key) {
		isExistData(key);
		redisTemplate.delete(key);
	}

	public void deleteRefreshToken(String refreshToken) {
		// delete 메서드 삭제 시 true 반환
		redisTemplate.delete(refreshToken);
	}

	// get AccessToken
	public String getAccessToken(String accessToken) {
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		return valueOperations.get(accessToken);
	}

}
