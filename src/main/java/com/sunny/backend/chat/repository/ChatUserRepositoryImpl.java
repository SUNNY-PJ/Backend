package com.sunny.backend.chat.repository;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sunny.backend.chat.domain.ChatMessage;

public class ChatUserRepositoryImpl extends QuerydslRepositorySupport implements ChatUserCustomRepository {
	private JPAQueryFactory queryFactory;

	public ChatUserRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		super(ChatMessage.class);
		this.queryFactory = jpaQueryFactory;
	}

	// @Override
	// public List<ChatRoomResponse> getChatRoomResponseByUserId(Long userId) {
	// 	return queryFactory.select(
	// 			Projections.constructor(ChatRoomResponse.class,
	// 				chatUser.chatRoom.id, chatUser.friend.id, chatUser.friend.name, chatUser.friend.profile
	// 			)
	// 		)
	// 		.from(chatUser, chatMessage)
	// 		.leftJoin(chatUser.chatRoom, chatMessage.chatRoom)
	// 		.orderBy(chatMessage.createdDate.desc())
	// 		.fetch();
	// }
}
