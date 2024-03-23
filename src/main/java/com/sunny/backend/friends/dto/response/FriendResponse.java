package com.sunny.backend.friends.dto.response;

import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.domain.FriendStatus;

import lombok.Builder;

@Builder
public record FriendResponse (
	Long friendId,
	Long userFriendId,
	String nickname,
	String profile,
	FriendStatus friendStatus
){
	public static FriendResponse from(Friend friend) {
		return FriendResponse.builder()
			.friendId(friend.getId())
			.userFriendId(friend.getUserFriend().getId())
			.nickname(friend.getUsers().getNickname())
			.profile(friend.getUsers().getProfile())
			.friendStatus(friend.getStatus())
			.build();
	}

}
