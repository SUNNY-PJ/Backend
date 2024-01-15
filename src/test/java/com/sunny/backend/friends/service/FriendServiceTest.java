package com.sunny.backend.friends.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.sunny.backend.common.CustomException;
import com.sunny.backend.dto.response.FriendCheckResponse;
import com.sunny.backend.dto.response.FriendResponse;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.domain.FriendStatus;
import com.sunny.backend.friends.exception.FriendErrorCode;
import com.sunny.backend.friends.repository.FriendRepository;
import com.sunny.backend.security.service.CustomUserDetailsService;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.user.Role;
import com.sunny.backend.user.Users;
import com.sunny.backend.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@SpringBootTest
class FriendServiceTest {
	@Autowired
	WebApplicationContext context;

	@Autowired
	UserRepository userRepository;

	@Autowired
	FriendRepository friendRepository;

	@Autowired
	FriendService friendService;

	@Autowired
	CustomUserDetailsService customUserDetailsService;

	CustomUserPrincipal customUserPrincipal;

	MockMvc mockMvc;
	Users user;
	Users userFriend;
	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders
			.webAppContextSetup(context)
			.apply(springSecurity())
			.build();
		setTestUsersAndIssueToken();
	}

	@Nested
	class 친구_목록_테스트 {
		@Test
		void 친구_목록_가져오기() {
			// given
			Friend friend = createFriend(user, userFriend, FriendStatus.APPROVE);
			createFriend(userFriend, user, FriendStatus.APPROVE);

			List<FriendResponse> expected = new ArrayList<>();
			expected.add(FriendResponse.from(friend));

			// when
			List<FriendResponse> actual = friendService.getFriends(customUserPrincipal);

			// then
			log.info(actual.toString());
			log.info(expected.toString());
			assertThat(actual.get(0))
				.extracting("friendsId", "name")
				.containsExactly(expected.get(0).friendsId(), expected.get(0).name());
		}
	}

	@Nested
	class 친구_신청_테스트 {
		@Test
		void 친구_신청_성공() {
			// given

			// when
			friendService.addFriend(customUserPrincipal, userFriend.getId());
			Optional<Friend> optionalFriend = friendRepository
				.findByUsers_IdAndUserFriend_Id(userFriend.getId(), user.getId());
			Friend friend = optionalFriend.get();

			// then
			assertThat(friend.getUserFriend().getId()).isEqualTo(user.getId());
			assertThat(friend.getStatus()).isEqualTo(FriendStatus.WAIT);
		}

		@Test
		void 신청한_친구한테_한번_더_신청하기() {
			// given
			createFriend(userFriend, user, FriendStatus.WAIT);

			// when
			ThrowingCallable create = () -> friendService.addFriend(customUserPrincipal, userFriend.getId());

			// then
			assertThatExceptionOfType(CustomException.class).isThrownBy(create)
				.withMessage(FriendErrorCode.FRIEND_NOT_APPROVE.getMessage());
		}

		@Test
		void 이미_친구인_친구에게_한번_더_신청하기() {
			// given
			createFriend(userFriend, user, FriendStatus.APPROVE);

			// when
			ThrowingCallable create = () -> friendService.addFriend(customUserPrincipal, userFriend.getId());

			// then
			assertThatExceptionOfType(CustomException.class).isThrownBy(create)
				.withMessage(FriendErrorCode.FRIEND_EXIST.getMessage());
		}
	}

	@Nested
	class 친구_끊기_테스트 {
		@Test
		void 친구_끊기_성공() {
			// given
			// 테스트를 위해서 승인된 데이터 저장
			Friend friend = createFriend(user, userFriend, FriendStatus.APPROVE);
			Friend friendUser = createFriend(userFriend, user, FriendStatus.APPROVE);

			// when
			friendService.deleteFriends(customUserPrincipal, friend.getId());

			// then
			Optional<Friend> friendAfterDeleted = friendRepository.findById(friend.getId());
			Optional<Friend> userFriendAfterDeleted = friendRepository.findById(friendUser.getId());
			assertThat(friendAfterDeleted).isEmpty();
			assertThat(userFriendAfterDeleted).isEmpty();
		}
	}

	@Nested
	class 친구_확인_테스트 {
		@Test
		void 친구_확인() {
			// given
			// 테스트를 위해서 승인된 데이터 저장
			createFriend(user, userFriend, FriendStatus.APPROVE);
			createFriend(userFriend, user, FriendStatus.APPROVE);

			// when
			FriendCheckResponse friendsCheckResponse = friendService.checkFriend(customUserPrincipal, userFriend.getId());

			// then
			assertThat(friendsCheckResponse.isFriend()).isTrue();
			assertThat(friendsCheckResponse.status()).isEqualTo(FriendStatus.APPROVE);
		}

		@Test
		void 승인대기_친구_확인() {
			// given
			// 테스트를 위해서 대기중인 데이터 저장
			createFriend(userFriend, user, FriendStatus.WAIT);

			// when
			FriendCheckResponse friendsCheckResponse = friendService.checkFriend(customUserPrincipal, userFriend.getId());

			// then
			assertThat(friendsCheckResponse.isFriend()).isFalse();
			assertThat(friendsCheckResponse.status()).isEqualTo(FriendStatus.WAIT);
		}

		@Test
		void 친구가_아닌_유저_확인() {
			// given

			// when
			FriendCheckResponse friendsCheckResponse = friendService.checkFriend(customUserPrincipal, userFriend.getId());

			// then
			assertThat(friendsCheckResponse.isFriend()).isFalse();
			assertThat(friendsCheckResponse.status()).isNull();
		}

	}

	private void setTestUsersAndIssueToken() {
		String userName = "유저이름";
		String userEmail = "user@naver.com";
		String friendName = "친구이름";
		String friendEmail = "UserFriend@naver.com";

		user = createUser(userName, userEmail);
		userFriend = createUser(friendName, friendEmail);

		// testUser의 토큰 반환
		customUserPrincipal = customUserDetailsService.loadUserByUsername(userEmail);
	}

	private Users createUser(String name, String email) {
		Users saveUser = Users.builder()
			.name(name)
			.email(email)
			.role(Role.USER)
			.build();
		return userRepository.save(saveUser);
	}

	private Friend createFriend(Users users, Users userFriend, FriendStatus status) {
		Friend saveFriend = Friend.builder()
			.users(users)
			.userFriend(userFriend)
			.status(status)
			.build();
		return friendRepository.save(saveFriend);
	}
}