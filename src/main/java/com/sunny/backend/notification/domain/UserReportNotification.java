package com.sunny.backend.notification.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
public class UserReportNotification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "users_id")
	private Users users; //경고 보낸 사람

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "warn_user_id")
	private Users warnUser; //경고 받은 사람 근데 이거 친구 아니어도 되니까
	@Column
	private String title;
	@Column
	private NotifiacationSubType subType;

	@Column
	private String body;
	@Column
	private String content;
	@Column
	private String reportContent;
	@Column
	private LocalDateTime createdAt;
	@Column
	private LocalDateTime reportCreatedAt;

}