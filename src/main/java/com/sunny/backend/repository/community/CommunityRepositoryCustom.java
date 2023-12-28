package com.sunny.backend.repository.community;

import com.sunny.backend.dto.response.community.CommunityResponse;
import com.sunny.backend.entity.BoardType;
import com.sunny.backend.entity.SortType;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;


public interface CommunityRepositoryCustom {


    Slice<CommunityResponse.PageResponse> getPageListWithSearch(SortType sortType, BoardType boardType, String searchText, Pageable pageable);

    Slice<CommunityResponse.PageResponse> getCommunityList(Pageable pageable);
}
