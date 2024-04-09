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

import com.sunny.backend.common.BaseTime;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.user.domain.Users;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityReport extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "users_id")
	private Users users;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "community_id")
	private Community community;

	@Column
	private String reason;

	@Column
	@Enumerated(value = EnumType.STRING)
	private ReportStatus status;

	private CommunityReport(Users users, Community community, String reason, ReportStatus status) {
		this.users = users;
		this.community = community;
		this.reason = reason;
		this.status = status;
	}

	public static CommunityReport of(Users users, Community community, String reason) {
		return new CommunityReport(users, community, reason, ReportStatus.WAIT);
	}

	public void validateWaitStatus() {
		if (status != ReportStatus.WAIT) {
			throw new CustomException(ALREADY_PROCESS);
		}
	}

	public void approveStatus() {
		status = ReportStatus.APPROVE;
	}
}
