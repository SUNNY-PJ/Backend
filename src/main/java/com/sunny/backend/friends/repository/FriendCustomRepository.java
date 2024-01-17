package com.sunny.backend.friends.repository;

import java.util.List;

import com.sunny.backend.friends.dto.response.FriendResponse;

public interface FriendCustomRepository {
	List<FriendResponse> getFriendResponse(Long userId);
}
