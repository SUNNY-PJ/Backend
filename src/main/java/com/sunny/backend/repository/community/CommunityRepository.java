package com.sunny.backend.repository.community;

import static com.sunny.backend.common.CommonErrorCode.COMMUNITY_NOT_FOUND;

import com.sunny.backend.common.CommonCustomException;
import com.sunny.backend.dto.response.community.CommunityResponse;
import com.sunny.backend.entity.Community;
import com.sunny.backend.entity.Consumption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Long>,
    CommunityRepositoryCustom {

    List<Community> findAllByUsers_Id(Long userId);

    default Community getById(Long id) {
        return findById(id)
            .orElseThrow(() -> new CommonCustomException(COMMUNITY_NOT_FOUND));
    }
}
