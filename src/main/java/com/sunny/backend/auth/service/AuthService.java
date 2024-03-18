package com.sunny.backend.auth.service;

import org.springframework.stereotype.Service;

import com.sunny.backend.auth.dto.TokenResponse;
import com.sunny.backend.auth.jwt.TokenProvider;
import com.sunny.backend.user.repository.UserRepository;
import com.sunny.backend.util.RedisUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final RedisUtil redisUtil;
	private final TokenProvider tokenProvider;
	private final UserRepository userRepository;

	//TODO 수정 필요할 수도, isUserRegistered
	public TokenResponse reissue(String refreshToken) {
		redisUtil.isExistData(refreshToken);

		String email = redisUtil.getData(refreshToken);
		userRepository.getByEmail(email);
		redisUtil.deleteData(refreshToken);
		return tokenProvider.createToken(email, "ROLE_USER",true);
	}
}
