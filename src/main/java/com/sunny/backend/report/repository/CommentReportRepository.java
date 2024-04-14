package com.sunny.backend.report.repository;

import static com.sunny.backend.report.exception.ReportErrorCode.*;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.sunny.backend.comment.domain.Comment;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.report.domain.CommentReport;
import com.sunny.backend.user.domain.Users;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
	void deleteAllByCommentInOrUsers(List<Comment> commentList, Users users);

	default CommentReport getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new CustomException(REPORT_COMMENT_NOT_FOUND));
	}

	@Transactional
	@Modifying
	@Query("UPDATE CommentReport c SET c.users = null WHERE c.users.id = :userId")
	void nullifyUsersId(Long userId);
}
