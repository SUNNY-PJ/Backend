package com.sunny.backend.competition.domain;

import static com.sunny.backend.competition.domain.CompetitionOutput.*;
import static com.sunny.backend.competition.exception.CompetitionErrorCode.*;
import static lombok.AccessLevel.*;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.friends.domain.Friend;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Competition {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; //경쟁 id

	@Column
	private String message; // 도발 메세지

	@Embedded
	private CompetitionOutput output; // 결과

	@Column
	private LocalDate startDate; // 시작 기간

	@Column
	private LocalDate endDate; // 종료 기간

	@Column
	private Long price; // 대결 금액

	@Column
	private String compensation; // 대결 보상

	@Column(name = "competition_status")
	@Enumerated(value = EnumType.STRING)
	private CompetitionStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "friend_id")
	private Friend friend;

	public boolean isEqualToCompetitionStatus(CompetitionStatus competitionStatus) {
		return status == competitionStatus;
	}

	public void updateStatus(CompetitionStatus status) {
		this.status = status;
	}

	public void validateReceiveUser(Long userId) {
		if (friend.getUsers().getId().equals(userId)) {
			throw new CustomException(COMPETITION_NOT_MYSELF);
		}
	}

	public void validateStatus() {
		if (status == CompetitionStatus.SEND) {
			throw new CustomException(COMPETITION_SEND);
		}
		if (status == CompetitionStatus.PROCEEDING) {
			throw new CustomException(COMPETITION_EXIST);
		}
	}

	public CompetitionStatus getStatus(Long id) {
		if (this.status != CompetitionStatus.SEND) {
			return this.status;
		} else {
			if (friend.getUsers().getId().equals(id)) {
				return CompetitionStatus.SEND;
			}
			return CompetitionStatus.RECEIVE;
		}
	}

	public void addDate(LocalDate startDate, LocalDate endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}

	private Competition(String message, CompetitionOutput output, LocalDate startDate, LocalDate endDate, Long price,
		String compensation,
		CompetitionStatus status, Friend friend) {
		this.message = message;
		this.output = output;
		this.startDate = startDate;
		this.endDate = endDate;
		this.price = price;
		this.compensation = compensation;
		this.status = status;
		this.friend = friend;
	}

	public static Competition of(
		String message,
		LocalDate startDate,
		LocalDate endDate,
		Long price,
		String compensation,
		Friend friend
	) {
		CompetitionOutput output = CompetitionOutput.from(COMPETITION_NONE_VALUE);
		return new Competition(message, output, startDate, endDate, price, compensation, CompetitionStatus.SEND,
			friend);
	}

}
