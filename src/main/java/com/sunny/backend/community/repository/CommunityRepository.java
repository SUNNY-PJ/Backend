package com.sunny.backend.community.repository;

import static com.sunny.backend.community.exception.CommunityErrorCode.*;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.community.domain.Community;

public interface CommunityRepository extends JpaRepository<Community, Long>,
	CommunityRepositoryCustom {
	List<Community> findAllByUsers_Id(Long userId);

	default Community getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new CustomException(COMMUNITY_NOT_FOUND));
	}
}
