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

import com.sunny.backend.common.CustomException;
import com.sunny.backend.common.ErrorCode;
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
public class Friends {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long friendsSn;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private Users users;

	@ManyToOne
	@JoinColumn(name = "friends_id")
	private Users friend;

	@Column
	@Enumerated(value = EnumType.STRING)
	private FriendStatus status;

	public void approveStatus() {
		status = FriendStatus.APPROVE;
	}

	public void validateFriendsByUser(Long userId, Long tokenUserId) {
		if(!userId.equals(tokenUserId)) {
			throw new CustomException(ErrorCode.TOKEN_INVALID);
		}
	}
}
