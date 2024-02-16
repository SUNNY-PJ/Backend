package com.sunny.backend.chat.repository;

import static com.querydsl.core.group.GroupBy.*;
import static com.sunny.backend.chat.domain.QChatMessage.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sunny.backend.chat.domain.ChatMessage;
import com.sunny.backend.chat.dto.response.ChatMessageResponse;
import com.sunny.backend.chat.dto.response.MessageResponse;

public class ChatMessageRepositoryImpl extends QuerydslRepositorySupport implements ChatMessageRepositoryCustom {

	private JPAQueryFactory queryFactory;

	public ChatMessageRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		super(ChatMessage.class);
		this.queryFactory = jpaQueryFactory;
	}

	@Override
	public List<ChatMessageResponse> getChatMessageList(Long chatRoomId, Integer size, Long chatMessageId) {
		return queryFactory.selectFrom(chatMessage)
			.where(chatMessage.chatRoom.id.eq(chatRoomId), ltChatMessageId(chatMessageId))
			.orderBy(chatMessage.createdDate.asc())
			// .offset(pageable.getOffset())
			.limit(size)
			.transform(
				groupBy(LocalDateTimeToString(chatMessage.createdDate)).list(
					Projections.constructor(ChatMessageResponse.class, LocalDateTimeToString(chatMessage.createdDate),
						list(Projections.constructor(MessageResponse.class, chatMessage.id, chatMessage.message,
							chatMessage.users.id.as("userId"), chatMessage.users.name, chatMessage.read,
							chatMessage.createdDate.as("time"))
						)
					)
				)
			);

		// boolean hasNext = false;
		// int dataSize = chatMessageResponses.stream().mapToInt(res -> res.messageResponses().size()).sum();
		// if (dataSize > pageable.getPageSize()) {
		// 	int size = chatMessageResponses.size();
		// 	int listSize = chatMessageResponses.get(size - 1).messageResponses().size();
		// 	chatMessageResponses.get(size - 1).messageResponses().remove(listSize - 1);
		// 	hasNext = true;
		// }
		//
		// return new SliceImpl<>(chatMessageResponses, pageable, hasNext);
	}

	private DateTemplate<String> LocalDateTimeToString(DateTimePath<LocalDateTime> localDateTime) {
		return Expressions.dateTemplate(
			String.class
			, "DATE_FORMAT({0}, {1})"
			, localDateTime
			, ConstantImpl.create("%Y-%m-%d"));
	}

	private BooleanExpression ltChatMessageId(Long chatMessageId) {
		if (chatMessageId == null) {
			return null;
		}
		return chatMessage.id.gt(chatMessageId);
	}
}
