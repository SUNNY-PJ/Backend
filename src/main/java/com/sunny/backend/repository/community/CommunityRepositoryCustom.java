package com.sunny.backend.repository.community;

import com.sunny.backend.dto.response.PageResponse;
import com.sunny.backend.entity.BoardType;
import com.sunny.backend.entity.SearchType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public interface CommunityRepositoryCustom {


    PageImpl<PageResponse> getQuestionListPageWithSearch(BoardType boardType, SearchType searchType, Pageable pageable);

    PageImpl<PageResponse> getCommunityList(Pageable pageable);
}
