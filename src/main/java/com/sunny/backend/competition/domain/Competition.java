package com.sunny.backend.competition.domain;

import static lombok.AccessLevel.*;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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

	@Column
	private LocalDate startDate; // 시작 기간

	@Column
	private LocalDate endDate; // 종료 기간

	@Column
	private Long price; // 대결 금액

	@Column
	private String compensation; // 대결 보상

	// @Embedded
	// private CompetitionOutput output; // 결과
	//
	// @Column(name = "competition_status")
	// @Enumerated(value = EnumType.STRING)
	// private CompetitionStatus status;

	// public boolean isEqualToCompetitionStatus(CompetitionStatus competitionStatus) {
	// 	return status == competitionStatus;
	// }
	//
	// public void updateStatus(CompetitionStatus status) {
	// 	this.status = status;
	// }

	// public void validateReceiveUser(Long userId) {
	// 	if (friend.getUsers().getId().equals(userId)) {
	// 		throw new CustomException(COMPETITION_NOT_MYSELF);
	// 	}
	// }

	// public void validateStatus() {
	// 	if (status == CompetitionStatus.SEND) {
	// 		throw new CustomException(COMPETITION_SEND);
	// 	}
	// 	if (status == CompetitionStatus.PROCEEDING) {
	// 		throw new CustomException(COMPETITION_EXIST);
	// 	}
	// }

	// public CompetitionStatus getStatus(Long id) {
	// 	if (this.status != CompetitionStatus.SEND) {
	// 		return this.status;
	// 	} else {
	// 		if (friend.getUsers().getId().equals(id)) {
	// 			return CompetitionStatus.SEND;
	// 		}
	// 		return CompetitionStatus.RECEIVE;
	// 	}
	// }

	// public void updateOutput(double userPercent, double userFriendPercent, Long userId, Long userFriendId) {
	// 	this.output.updateOutput(userPercent, userFriendPercent, userId, userFriendId);
	// }

	private Competition(String message, LocalDate startDate, LocalDate endDate, Long price,
		String compensation) {
		this.message = message;
		this.startDate = startDate;
		this.endDate = endDate;
		this.price = price;
		this.compensation = compensation;
	}

	public static Competition of(
		String message,
		LocalDate startDate,
		LocalDate endDate,
		Long price,
		String compensation
	) {
		return new Competition(message, startDate, endDate, price, compensation);
	}

}
