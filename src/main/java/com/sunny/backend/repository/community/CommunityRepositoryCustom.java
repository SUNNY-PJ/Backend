package com.sunny.backend.repository.community;

import com.sunny.backend.dto.response.community.CommunityResponse;
import com.sunny.backend.entity.BoardType;
import com.sunny.backend.entity.SearchType;
import com.sunny.backend.entity.SortType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public interface CommunityRepositoryCustom {


    PageImpl<CommunityResponse.PageResponse> getPageListWithSearch(SortType sortType, BoardType boardType, SearchType searchType, Pageable pageable);

    PageImpl<CommunityResponse.PageResponse> getCommunityList(Pageable pageable);
}
