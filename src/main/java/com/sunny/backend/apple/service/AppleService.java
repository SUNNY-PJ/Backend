package com.sunny.backend.apple.service;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.Security;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sunny.backend.auth.dto.UserNameResponse;
import com.sunny.backend.auth.dto.UserRequest;
import com.sunny.backend.auth.exception.UserErrorCode;
import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.auth.jwt.TokenProvider;
import com.sunny.backend.comment.repository.CommentRepository;
import com.sunny.backend.common.config.AppleProperties;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.competition.repository.CompetitionRepository;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.domain.FriendCompetition;
import com.sunny.backend.friends.repository.FriendCompetitionRepository;
import com.sunny.backend.friends.repository.FriendRepository;
import com.sunny.backend.notification.repository.CommentNotificationRepository;
import com.sunny.backend.notification.repository.FriendsNotificationRepository;
import com.sunny.backend.notification.repository.NotificationRepository;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.repository.UserRepository;
import com.sunny.backend.util.RedisUtil;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppleService {

	private final AppleAuthClient appleAuthClient;
	private final AppleProperties appleProperties;
	private final UserRepository userRepository;
	private final CommentRepository commentRepository;
	private final CommentNotificationRepository commentNotificationRepository;
	private final FriendsNotificationRepository friendsNotificationRepository;
	private final FriendRepository friendRepository;
	private final CompetitionRepository competitionRepository;
	private final NotificationRepository notificationRepository;
	private final ResponseService responseService;
	private final RedisUtil redisUtil;
	private final FriendCompetitionRepository friendCompetitionRepository;

	private final TokenProvider tokenProvider;

	public String generateClientSecret() throws IOException {
		LocalDateTime expiration = LocalDateTime.now().plusMinutes(5);
		Map<String, Object> jwtHeader = new HashMap<>();
		jwtHeader.put("kid", appleProperties.getKeyId());
		jwtHeader.put("alg", "ES256");

		return Jwts.builder()
			.setHeaderParams(jwtHeader)
			.setIssuer(appleProperties.getTeamId())
			.setIssuedAt(new Date(System.currentTimeMillis())) // 발행 시간 - UNIX 시간
			.setExpiration(Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant())) // 만료 시간
			.setAudience("https://appleid.apple.com")
			.setSubject(appleProperties.getClientId())
			.signWith(SignatureAlgorithm.ES256, getPrivateKey())
			.compact();
	}

	private PrivateKey getPrivateKey() {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
		try {
			byte[] privateKeyBytes = Base64.getDecoder().decode(appleProperties.getPrivateKey());
			PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(privateKeyBytes);
			return converter.getPrivateKey(privateKeyInfo);
		} catch (Exception e) {
			throw new RuntimeException("Error converting private key from String", e);
		}
	}

	@Transactional
	public UserNameResponse changeNickname(CustomUserPrincipal customUserPrincipal, String name) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		Optional<Users> optionalUsers = userRepository.findByNickname(name);
		if (optionalUsers.isPresent()) {
			throw new CustomException(UserErrorCode.NICKNAME_IN_USE);
		}
		user.updateName(name);
		userRepository.save(user);
		return new UserNameResponse(user.getNickname());
	}

	public ResponseEntity<?> reissue(String refreshToken) {
		redisUtil.isExistData(refreshToken);
		String email = redisUtil.getData(refreshToken);
		userRepository.getByEmail(email);
		return tokenProvider.reissue(refreshToken, email);
	}

	public ResponseEntity<CommonResponse.GeneralResponse> logout(UserRequest logout) {
		int status =
			tokenProvider.logout(logout) ? HttpStatus.OK.value() : HttpStatus.BAD_REQUEST.value();
		String message = tokenProvider.logout(logout) ? "logout 성공" : "logout 실패";
		return responseService.getGeneralResponse(status, message);
	}

	@Transactional
	public ResponseEntity<CommonResponse.GeneralResponse> revokeToken(Long userId, String code) {
		Users users = userRepository.getById(userId);
		log.info("code={}", code);
		try {
			ResponseEntity<String> response = appleAuthClient.revokeToken(
				appleProperties.getClientId(),
				generateClientSecret(),
				code,
				"access_token"
			);

			if (response.getStatusCode().is2xxSuccessful()) {
				log.info("Apple token 삭제 성공");
				notificationRepository.deleteByUsers(users);

				for (Friend friend : friendRepository.findByUsers(users)) {
					List<FriendCompetition> friendCompetitions = friendCompetitionRepository.getByFriendAndCompetition(
						friend.getId(), null);
					friendCompetitionRepository.deleteAllByFriend(friend);
					List<Long> competitionIds = friendCompetitions.stream()
						.map(friendCompetition -> friendCompetition.getCompetition().getId())
						.toList();
					competitionRepository.deleteAllById(competitionIds);
				}
				friendsNotificationRepository.deleteByUsersOrFriend(users, users);
				friendRepository.deleteByUsersOrUserFriend(users, users);
				commentNotificationRepository.deleteByUsers(users);
				commentRepository.nullifyUsersId(users.getId());
				userRepository.deleteById(users.getId());
				log.info("Apple token 삭제 성공 code={}", HttpStatus.OK.value());
				return responseService.getGeneralResponse(HttpStatus.OK.value(), "탈퇴 성공");
			} else {
				log.error("Apple token 삭제 실패");
				log.info("Apple token 삭제 실패 code={}", HttpStatus.INTERNAL_SERVER_ERROR.value());
				return responseService.getGeneralResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "탈퇴 실패");
			}
		} catch (Exception e) {
			log.error("Apple token 탈퇴 중 오류: {}", e.getMessage(), e);
			log.info("Apple token 삭제 오류 code={}", HttpStatus.INTERNAL_SERVER_ERROR.value());
			return responseService.getGeneralResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "탈퇴 실패");
		}
	}
}