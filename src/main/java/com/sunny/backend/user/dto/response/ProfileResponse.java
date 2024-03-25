package com.sunny.backend.user.dto.response;

import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.domain.FriendStatus;
import com.sunny.backend.user.domain.Users;

import lombok.Builder;

@Builder
public record ProfileResponse(
	Long id,
	String name,
	String profile,
	boolean owner,
	Long friendId,
	FriendStatus friendStatus
) {
	public static ProfileResponse from(Users users) {
		return ProfileResponse.builder()
			.id(users.getId())
			.name(users.getNickname())
			.profile(users.getProfile())
			.owner(true)
			.build();
	}

	public static ProfileResponse of(Users users, FriendStatus friendStatus, Friend friend) {
		return new ProfileResponse(
			users.getId(),
			users.getNickname(),
			users.getProfile(),
			false,
			friend.getId(),
			friendStatus
		);
	}

	public static ProfileResponse fromNotFriend(Users users) {
		return ProfileResponse.builder()
			.id(users.getId())
			.name(users.getNickname())
			.profile(users.getProfile())
			.owner(false)
			.friendStatus(FriendStatus.NONE)
			.build();
	}
}
