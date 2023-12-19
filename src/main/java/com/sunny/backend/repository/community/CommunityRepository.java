package com.sunny.backend.repository.community;

import com.sunny.backend.dto.response.community.CommunityResponse;
import com.sunny.backend.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community,Long>, CommunityRepositoryCustom {
    List<Community> findAllByUsers_Id (Long userId);
}
