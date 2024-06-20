package com.sunny.backend.util;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.sunny.backend.common.CommonErrorCode;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.community.repository.CommunityRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisUtil {
	private final RedisTemplate<String, String> redisTemplate;
	private final CommunityRepository communityRepository;

	public String getData(String key) {
		log.info(redisTemplate.opsForValue().get(key));
		return redisTemplate.opsForValue().get(key);
	}

	public void isExistData(String key) {
		if (redisTemplate.opsForValue().get(key) == null) {
			throw new CustomException(CommonErrorCode.TOKEN_EXPIRED);
		}
	}

	public void setValuesWithTimeout(String key, String value, long timeout) {
		try {
			redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MILLISECONDS);
			log.info("Redis Value 저장 성공!");
		} catch (Exception e) {
			log.info("Redis Value 저장 에러!: " + e.getMessage());
			e.printStackTrace();
		}
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

	//user가 게시글을 봤는지 체크
	public boolean hasUserViewedCommunity(Long userId, Long postId) {
		String userKey = "user:" + userId;
		return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(userKey, postId.toString()));
	}

	//최초 게시글 조회 시 redis에 key,value,expire 추가
	public void addUserView(Long userId, Long postId) {
		String userKey = "user:" + userId;
		redisTemplate.opsForSet().add(userKey, postId.toString());
		redisTemplate.expire(userKey, 24, TimeUnit.HOURS);
	}

	// 사용자가 조회한 게시글인지 아닌지 check
	public void incrementCommunityViewIfNotViewed(Long userId, Long postId) {
		Community community = communityRepository.getById(postId);
		if (!hasUserViewedCommunity(userId, postId)) {
			addUserView(userId, postId);
			community.increaseView();
		}
	}
}
