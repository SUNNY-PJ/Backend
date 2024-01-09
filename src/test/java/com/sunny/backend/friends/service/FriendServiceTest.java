package com.sunny.backend.friends.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.checkerframework.checker.units.qual.N;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.sunny.backend.dto.response.FriendsResponse;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.domain.FriendStatus;
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
		void 승인된_친구_목록_가져오기() {
			// given
			Friend friend = createFriend(user, userFriend, FriendStatus.APPROVE);
			createFriend(userFriend, user, FriendStatus.APPROVE);

			List<FriendsResponse> expected = new ArrayList<>();
			expected.add(FriendsResponse.from(friend));

			// when
			List<FriendsResponse> actual = friendService.getFriends(customUserPrincipal, FriendStatus.APPROVE);

			// then
			log.info(actual.toString());
			log.info(expected.toString());
			assertThat(actual.get(0))
				.extracting("friendsId", "name")
				.containsExactly(expected.get(0).friendsId(), expected.get(0).name());
		}

		@Test
		void 대기중인_친구_목록_가져오기() {
			// given
			Friend friend = createFriend(user, userFriend, FriendStatus.WAIT);

			List<FriendsResponse> expected = new ArrayList<>();
			expected.add(FriendsResponse.from(friend));

			// when
			List<FriendsResponse> actual = friendService.getFriends(customUserPrincipal, FriendStatus.WAIT);

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
			friendService.addFriend(customUserPrincipal, userFriend.getId());
			// when
			friendService.addFriend(customUserPrincipal, userFriend.getId());
			// Optional<Friend> optionalFriend = friendRepository
			// 	.findByUsers_IdAndUserFriend_Id(userFriend.getId(), user.getId());
			// Friend friend = optionalFriend.get();

			// then
			// assertThat(friend.getUserFriend().getId()).isEqualTo(user.getId());
			// assertThat(friend.getStatus()).isEqualTo(FriendStatus.WAIT);
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

	// private void setFriend() {
	// 	createFriend(user, userFriend, FriendStatus.APPROVE);
	// 	createFriend(userFriend, user, FriendStatus.APPROVE);
	// }
}