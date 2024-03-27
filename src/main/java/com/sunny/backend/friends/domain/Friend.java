package com.sunny.backend.friends.domain;

import static com.sunny.backend.friends.exception.FriendErrorCode.*;
import static lombok.AccessLevel.*;

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
import com.sunny.backend.user.domain.Users;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Friend {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private Users users;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_friend_id")
	private Users userFriend;

	@Column(name = "friend_status")
	@Enumerated(value = EnumType.STRING)
	private FriendStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "competition_id")
	private Competition competition;

	private Friend(Users users, Users userFriend, FriendStatus status) {
		this.users = users;
		this.userFriend = userFriend;
		this.status = status;
	}

	public static Friend of(Users users, Users userFriend, FriendStatus status) {
		return new Friend(users, userFriend, status);
	}

	public void addCompetition(Competition competition) {
		this.competition = competition;
	}

	public void updateFriendStatus(FriendStatus friendStatus) {
		status = friendStatus;
	}

	public boolean isEqualToFriendStatus(FriendStatus friendStatus) {
		return status == friendStatus;
	}

	public boolean hasCompetition() {
		return competition != null;
	}

	public void validateCompetition() {
		if (competition == null) {
			throw new CustomException(FRIEND_NOT_COMPETITION);
		}
	}

	public void validateUser(Long tokenUserId) {
		if (!users.getId().equals(tokenUserId)) {
			throw new CustomException(CommonErrorCode.TOKEN_INVALID);
		}
	}

	public void validateCompetitionStatus() {
		if (competition != null) {
			competition.validateStatus();
		}
	}
}
