package com.sunny.backend.community.repository;

import com.sunny.backend.dto.response.community.CommunityResponse;
import com.sunny.backend.community.domain.BoardType;
import com.sunny.backend.community.domain.SortType;

import com.sunny.backend.dto.response.community.CommunityResponse.PageResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;


public interface CommunityRepositoryCustom {

    List<PageResponse> paginationNoOffsetBuilder(Long communityId,
        SortType sortType, BoardType boardType, String searchText, int pageSize);
}
