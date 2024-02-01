package com.sunny.backend.community.repository;

import com.sunny.backend.community.domain.BoardType;
import com.sunny.backend.community.domain.SortType;

import com.sunny.backend.community.dto.response.CommunityResponse.PageResponse;
import com.sunny.backend.user.domain.Users;
import java.util.List;


public interface CommunityRepositoryCustom {
    List<PageResponse> paginationNoOffsetBuilder(Users customUserPrincipal,Long communityId,
        SortType sortType, BoardType boardType, String searchText, int pageSize);
}
