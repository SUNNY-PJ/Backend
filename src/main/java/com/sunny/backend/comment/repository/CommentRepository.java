package com.sunny.backend.comment.repository;

import static com.sunny.backend.community.exception.CommunityErrorCode.*;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.sunny.backend.comment.domain.Comment;
import com.sunny.backend.common.exception.CustomException;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentCustomRepository {
	List<Comment> findAllByUsers_Id(Long userId);

	List<Comment> findAllByCommunity_Id(Long communityId);

	List<Comment> findByUsers_Id(Long userId);

	default Comment getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new CustomException(COMMUNITY_NOT_FOUND));
	}

	@Transactional
	@Modifying
	@Query("UPDATE Comment c SET c.users = null WHERE c.users.id = :userId")
	void


	nullifyUsersId(@Param(value = "userId") Long userId);

	@Transactional
	@Modifying
	@Query("UPDATE CommentReport c SET c.users = null WHERE c.users.id = :userId")
	void nullifyCommentReportUsersId(Long userId);

}


