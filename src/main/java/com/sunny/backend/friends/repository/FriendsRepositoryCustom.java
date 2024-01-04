package com.sunny.backend.friends.repository;

import java.util.List;

import com.sunny.backend.dto.response.FriendsResponse;
import com.sunny.backend.friends.domain.FriendStatus;

public interface FriendsRepositoryCustom {
	List<FriendsResponse> getFindUserIdAndApproveType(Long userId, FriendStatus friendStatus);
}
