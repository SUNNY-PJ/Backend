package com.sunny.backend.friends.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.sunny.backend.common.CommonCustomException;
import com.sunny.backend.common.CommonErrorCode;
import com.sunny.backend.common.CustomException;
import com.sunny.backend.friends.exception.FriendErrorCode;
import com.sunny.backend.user.Users;

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

	@ManyToOne
	@JoinColumn(name = "user_id")
	private Users users;

	@ManyToOne
	@JoinColumn(name = "user_friends_id")
	private Users userFriend;

	@Column
	@Enumerated(value = EnumType.STRING)
	private FriendStatus status;

	public void approveStatus() {
		status = FriendStatus.APPROVE;
	}

	public void switchStatus() {
		if(status.equals(FriendStatus.WAIT)) {
			throw new CustomException(FriendErrorCode.FRIEND_NOT_APPROVE);
		}
		if(status.equals(FriendStatus.APPROVE)) {
			throw new CustomException(FriendErrorCode.FRIEND_EXIST);
		}
	}

	public void validateFriendsByUser(Long userId, Long tokenUserId) {
		if(!userId.equals(tokenUserId)) {
			throw new CommonCustomException(CommonErrorCode.TOKEN_INVALID);
		}
	}
}
