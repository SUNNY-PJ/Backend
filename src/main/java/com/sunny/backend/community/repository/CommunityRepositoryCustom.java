package com.sunny.backend.community.repository;

import java.util.List;

import com.sunny.backend.community.domain.BoardType;
import com.sunny.backend.community.domain.SortType;
import com.sunny.backend.community.dto.response.CommunityPageResponse;
import com.sunny.backend.user.domain.Users;

public interface CommunityRepositoryCustom {
	List<CommunityPageResponse> paginationNoOffsetBuilder(Users customUserPrincipal, Long communityId,
		SortType sortType, BoardType boardType, String searchText, int pageSize);
}
