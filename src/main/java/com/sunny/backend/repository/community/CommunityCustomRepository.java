package com.sunny.backend.repository.community;

import com.sunny.backend.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommunityCustomRepository {
    List<PageResponse> findPageCommunity(Pageable pageable, String title, String contents);
}
