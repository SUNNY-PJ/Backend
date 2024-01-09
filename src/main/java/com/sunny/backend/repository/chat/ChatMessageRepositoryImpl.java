package com.sunny.backend.repository.chat;

import static com.querydsl.core.group.GroupBy.*;
import static com.sunny.backend.entity.chat.QChatMessage.*;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sunny.backend.dto.request.chat.ChatMessageResponse;
import com.sunny.backend.entity.chat.ChatMessage;

public class ChatMessageRepositoryImpl extends QuerydslRepositorySupport implements ChatMessageRepositoryCustom {

	private JPAQueryFactory queryFactory;

	public ChatMessageRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		super(ChatMessage.class);
		this.queryFactory = jpaQueryFactory;
	}

	@Override
	public List<ChatMessageResponse> getChatMessageList(Long chatRoomId) {
		return queryFactory.selectFrom(chatMessage)
			.where(chatMessage.chatRoom.id.eq(chatRoomId))
			.orderBy(chatMessage.createdDate.asc())
			.transform(
				groupBy(						Expressions.dateTemplate(
					String.class
					, "DATE_FORMAT({0}, {1})"
					, chatMessage.createdDate
					, ConstantImpl.create("%Y-%m-%d"))).list(
					Projections.constructor(ChatMessageResponse.class,
						Expressions.dateTemplate(
							String.class
							, "DATE_FORMAT({0}, {1})"
							, chatMessage.createdDate
							, ConstantImpl.create("%Y-%m-%d")),
						list(Projections.constructor(ChatMessageResponse.DayMessage.class, chatMessage.message,
							chatMessage.users.id.as("userId"), chatMessage.users.name, chatMessage.createdDate.as("time"))
						)
					)
				)
			);
	}
}
