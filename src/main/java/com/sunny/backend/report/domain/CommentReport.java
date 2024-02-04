package com.sunny.backend.report.domain;

import static com.sunny.backend.report.exception.ReportErrorCode.*;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.sunny.backend.comment.domain.Comment;
import com.sunny.backend.common.BaseTime;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.friends.domain.Status;
import com.sunny.backend.user.domain.Users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentReport extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
	@JoinColumn(name= "users_id")
	private Users users;

	@ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
	@JoinColumn(name= "comment_id")
	private Comment comment;

	@Column
	private String reason;

	@Column
	@Enumerated(value = EnumType.STRING)
	private Status status;

	public void isWait() {
		if(status != Status.WAIT) {
			throw new CustomException(ALREADY_PROCESS);
		}
	}

	public void approveStatus() {
		status = Status.APPROVE;
	}
}
