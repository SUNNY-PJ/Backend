package com.sunny.backend.repository.friends;

import java.util.List;

import com.sunny.backend.dto.response.FriendsResponse;
import com.sunny.backend.entity.friends.ApproveType;

public interface FriendsRepositoryCustom {
	List<FriendsResponse> getFindUserIdAndApproveType(Long userId, ApproveType approveType);
}
