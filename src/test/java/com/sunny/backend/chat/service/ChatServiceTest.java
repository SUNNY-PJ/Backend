package com.sunny.backend.chat.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.auth.service.CustomUserDetailsService;
import com.sunny.backend.chat.domain.ChatMessage;
import com.sunny.backend.chat.domain.ChatRoom;
import com.sunny.backend.chat.domain.ChatUser;
import com.sunny.backend.chat.dto.response.ChatMessageResponse;
import com.sunny.backend.chat.dto.response.ChatRoomResponse;
import com.sunny.backend.chat.repository.ChatMessageRepository;
import com.sunny.backend.chat.repository.ChatRoomRepository;
import com.sunny.backend.chat.repository.ChatUserRepository;
import com.sunny.backend.user.domain.Role;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@SpringBootTest
class ChatServiceTest {
	@Autowired
	WebApplicationContext context;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ChatRoomRepository chatRoomRepository;

	@Autowired
	ChatUserRepository chatUserRepository;

	@Autowired
	ChatMessageRepository chatMessageRepository;

	@Autowired
	ChatService chatService;

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
	class 채팅_가져오기_테스트 {
		@Test
		void 대화_목록_가져오기() {
			// given
			ChatRoom chatRoom = createChatRoom();
			IntStream.rangeClosed(1, 5).forEachOrdered(i -> createChatMessage(i + "번 메세지", user, chatRoom));
			Pageable pageable = PageRequest.of(0, 5);

			// when
			List<ChatMessageResponse> actual = chatService.getChatMessageList(customUserPrincipal, chatRoom.getId(),
				50, null);

			// then
			assertThat(actual.get(0).getMessageResponses().size()).isEqualTo(pageable.getPageSize());
			assertThat(actual.get(0).getMessageResponses().get(0))
				.extracting("message", "userId")
				.containsExactly("1번 메세지", user.getId());
		}

		@Test
		void 채팅방_목록_가져오기() {
			// given
			ChatRoom chatRoom = createChatRoom();
			createChatUser(user, userFriend, chatRoom);

			// when
			List<ChatRoomResponse> actual = chatService.getChatRoomList(customUserPrincipal);

			// then
			assertThat(actual.get(0))
				.extracting("chatRoomId", "userFriendId")
				.containsExactly(chatRoom.getId(), userFriend.getId());
		}
	}

	@Nested
	class 채팅_삭제_테스트 {
		@Test
		void 채팅방_삭제() {
			// given
			ChatRoom chatRoom = createChatRoom();
			ChatUser chatUser = createChatUser(user, userFriend, chatRoom);
			createChatUser(userFriend, user, chatRoom);
			// when
			chatService.deleteChatRoom(chatUser.getId());

			// then
			Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(chatRoom.getId());
			assertThat(chatRoomOptional).isPresent();
			Optional<ChatUser> chatUserAfterDeleted = chatUserRepository.findById(chatUser.getId());
			assertThat(chatUserAfterDeleted).isEmpty();
		}

		@Test
		void 채팅방_둘_다_삭제() {
			// given
			ChatRoom chatRoom = createChatRoom();
			ChatUser chatUser = createChatUser(user, userFriend, chatRoom);
			// when
			chatService.deleteChatRoom(chatUser.getId());

			// then
			Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(chatRoom.getId());
			assertThat(chatRoomOptional).isEmpty();
			Optional<ChatUser> chatUserAfterDeleted = chatUserRepository.findById(chatUser.getId());
			assertThat(chatUserAfterDeleted).isEmpty();
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

	private ChatRoom createChatRoom() {
		return chatRoomRepository.save(new ChatRoom(2));
	}

	private ChatMessage createChatMessage(String message, Users user, ChatRoom chatRoom) {
		ChatMessage chatMessage = new ChatMessage(message, user, chatRoom);
		return chatMessageRepository.save(chatMessage);
	}

	private ChatUser createChatUser(Users user, Users userFriend, ChatRoom chatRoom) {
		return chatUserRepository.save(new ChatUser(user, userFriend, chatRoom));
	}
}