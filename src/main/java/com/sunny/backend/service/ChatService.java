package com.sunny.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.dto.request.chat.ChatSendMessage;
import com.sunny.backend.dto.request.chat.ChatMessageResponse;
import com.sunny.backend.dto.request.chat.ChatRoomResponse;
import com.sunny.backend.entity.chat.ChatMessage;
import com.sunny.backend.entity.chat.ChatRoom;
import com.sunny.backend.entity.chat.ChatUser;
import com.sunny.backend.repository.chat.ChatMessageRepository;
import com.sunny.backend.repository.chat.ChatRoomRepository;
import com.sunny.backend.repository.chat.ChatUserRepository;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.user.Users;
import com.sunny.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {
	private final ResponseService responseService;
	private final UserRepository userRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final ChatUserRepository chatUserRepository;
	private final ChatMessageRepository chatMessageRepository;
	private final SimpMessagingTemplate template;

	public ResponseEntity<CommonResponse.ListResponse<ChatMessageResponse>> getChatMessageList(Long chatRoomId) {
		List<ChatMessageResponse> list = chatMessageRepository.getChatMessageList(chatRoomId);
		return responseService.getListResponse(HttpStatus.OK.value(), list, "채팅 대화 조회");
	}

	public ResponseEntity<CommonResponse.ListResponse<ChatRoomResponse>> getChatRoomList(CustomUserPrincipal customUserPrincipal) {
		List<ChatRoomResponse> list = new ArrayList<>();
		List<ChatUser> chatUserList = chatUserRepository.findByUsers_Id(customUserPrincipal.getUsers().getId());
		for (ChatUser chatUser : chatUserList) {
			list.add(ChatRoomResponse.builder()
					.chatUserId(chatUser.getId())
					.chatRoomId(chatUser.getChatRoom().getId())
					.friendName(chatUser.getFriend().getName())
				.build());
		}
		return responseService.getListResponse(HttpStatus.OK.value(), list, "채팅방 목록 조회");
	}

	// @Transactional
	// public ResponseEntity<CommonResponse.GeneralResponse> createChatRoom(CustomUserPrincipal customUserPrincipal,
	// 	Long friendId) {
	// 	Users users = customUserPrincipal.getUsers();
	// 	Users friend = userRepository.findById(friendId)
	// 		.orElseThrow(() -> new IllegalArgumentException("Not Found Id" + friendId));
	//
	// 	if(chatUserRepository.existsByUsers_IdAndFriend_Id(users.getId(), friend.getId())) {
	// 		// 이미 채팅방이 존재합니다.
	// 	} else {
	// 		ChatRoom chatRoom = chatRoomRepository.save(new ChatRoom(2));
	// 		if(!chatUserRepository.existsByFriend_IdAndUsers_Id(friend.getId(), users.getId())) {
	// 			chatUserRepository.save(new ChatUser(users, friend, chatRoom));
	// 			chatUserRepository.save(new ChatUser(friend, users, chatRoom));
	// 		}
	// 	}
	// 	return responseService.getGeneralResponse(HttpStatus.OK.value(), "채팅방 생성 성공");
	// }

	@Transactional
	public ResponseEntity<CommonResponse.GeneralResponse> deleteChatRoom(Long chatUserId) {
		ChatUser chatUser = chatUserRepository.findById(chatUserId)
			.orElseThrow(() -> new IllegalArgumentException("Not Found Id" + chatUserId));

		Long count = chatUserRepository.countByChatRoom_Id(chatUser.getChatRoom().getId());
		if(count==1) {
			chatRoomRepository.deleteById(chatUser.getChatRoom().getId());
		}
		chatUserRepository.deleteById(chatUserId);
		return responseService.getGeneralResponse(HttpStatus.OK.value(), "채팅방 삭제 성공");
	}

	public void createChatMsg(ChatSendMessage chatSendMessage, String email) {
		Users users = userRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("Not Found Email" + email));
		Users friend = userRepository.findById(chatSendMessage.getFriendId())
			.orElseThrow(() -> new IllegalArgumentException("Not Found Id" + chatSendMessage.getFriendId()));
		ChatRoom chatRoom;

		// 0인 경우 채팅방 생성
		if(chatSendMessage.getChatRoomId() == 0) {	
			Optional<ChatUser> chatUserOptional = chatUserRepository
				.findByFriend_IdAndUsers_Id(chatSendMessage.getFriendId(), users.getId());
			if(chatUserOptional.isPresent()) {	// 친구의 채팅밥이 있는지 확인
				ChatUser chatUser = chatUserOptional.get();
				chatRoom = chatUser.getChatRoom();
			} else {
				chatRoom = chatRoomRepository.save(new ChatRoom(2));
				chatUserRepository.save(new ChatUser(friend, users, chatRoom)); // 친구 채팅방
			}
			chatUserRepository.save(new ChatUser(users, friend, chatRoom)); // 본인 채팅방
		} else {
			chatRoom = chatRoomRepository.findById(chatSendMessage.getChatRoomId())
				.orElseThrow(() -> new IllegalArgumentException("Not Found Id" + chatSendMessage.getChatRoomId()));
		}
		ChatMessage chatMessage = new ChatMessage(chatSendMessage.getMessage(), users, chatRoom);
		chatMessageRepository.save(chatMessage);
		template.convertAndSend("/sub/room/" + chatRoom.getId(), chatSendMessage.getMessage());
	}
}
