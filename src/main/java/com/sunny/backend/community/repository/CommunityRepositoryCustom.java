package com.sunny.backend.community.repository;

import com.sunny.backend.community.domain.BoardType;
import com.sunny.backend.community.domain.SortType;

import com.sunny.backend.community.dto.response.CommunityResponse.PageResponse;
import java.util.List;


public interface CommunityRepositoryCustom {

    List<PageResponse> paginationNoOffsetBuilder(Long communityId,
        SortType sortType, BoardType boardType, String searchText, int pageSize);
}
