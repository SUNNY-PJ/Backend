package com.sunny.backend.friends.domain;

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

import com.sunny.backend.common.CommonErrorCode;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.competition.domain.Competition;
import com.sunny.backend.friends.exception.FriendErrorCode;
import com.sunny.backend.user.domain.Users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Friend {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "user_id")
	private Users users;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "user_friend_id")
	private Users userFriend;

	@Column
	@Enumerated(value = EnumType.STRING)
	private Status status;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "competition_id")
	private Competition competition;

	public void addCompetition(Competition competition) {
		this.competition = competition;
	}

	public void approveStatus() {
		status = Status.APPROVE;
	}

	public boolean isApproveStatus() {
		return status.equals(Status.APPROVE);
	}

	public void validateStatus() {
		if (status.equals(Status.WAIT)) {
			throw new CustomException(FriendErrorCode.FRIEND_NOT_APPROVE);
		}
		if (status.equals(Status.APPROVE)) {
			throw new CustomException(FriendErrorCode.FRIEND_EXIST);
		}
	}

	public void validateFriendsByUser(Long userId, Long tokenUserId) {
		if (!userId.equals(tokenUserId)) {
			throw new CustomException(CommonErrorCode.TOKEN_INVALID);
		}
	}
}
