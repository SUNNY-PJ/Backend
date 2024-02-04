package com.sunny.backend.declaration.repository;

import static com.sunny.backend.declaration.exception.DeclarationErrorCode.*;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.declaration.domain.CommunityDeclaration;

public interface CommunityDeclarationRepository extends JpaRepository<CommunityDeclaration, Long> {
	default CommunityDeclaration getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new CustomException(DECLARE_COMMUNITY_NOT_FOUND));
	}
}
