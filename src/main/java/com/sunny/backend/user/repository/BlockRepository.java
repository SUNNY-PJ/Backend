package com.sunny.backend.user.repository;

import static com.sunny.backend.user.exception.BlockListErrorCode.*;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.user.domain.UsersBlock;

@Repository
public interface BlockRepository extends JpaRepository<UsersBlock, Long> {
	List<UsersBlock> findAllByUsers_Id(Long userId);

	List<UsersBlock> findAllByBlockedUser_Id(Long userId);

	default UsersBlock getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new CustomException(BLOCK_LIST_NOT_FOUND));
	}

}
