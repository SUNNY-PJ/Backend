package com.sunny.backend.community.repository;

import com.sunny.backend.common.CustomException;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.community.exception.CommunityErrorCode;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community,Long>, CommunityRepositoryCustom {
    List<Community> findAllByUsers_Id (Long userId);

    default Community getById(Long id) {
        return findById(id)
            .orElseThrow(() -> new CustomException(CommunityErrorCode.COMMUNITY_NOT_FOUND));
    }
}
