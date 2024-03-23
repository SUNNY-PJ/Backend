package com.sunny.backend.competition.domain;

import static com.sunny.backend.competition.domain.CompetitionOutput.*;
import static lombok.AccessLevel.*;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.competition.exception.CompetitionErrorCode;
import com.sunny.backend.friends.domain.FriendStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
	private Integer day;

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

	public void approveStatus() {
		status = CompetitionStatus.PROCEEDING;
	}

	public void updateOutput(Long output) {
		this.output = CompetitionOutput.from(output);
	}

	public void validateStatus() {
		if (status.equals(CompetitionStatus.PENDING)) {
			throw new CustomException(CompetitionErrorCode.COMPETITION_NOT_APPROVE);
		}
		if (status.equals(CompetitionStatus.PROCEEDING)) {
			throw new CustomException(CompetitionErrorCode.COMPETITION_EXIST);
		}
	}

	public void addDate(LocalDate startDate, LocalDate endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}


	private Competition(String message, CompetitionOutput output, Integer day, Long price, String compensation, CompetitionStatus status) {
		this.message = message;
		this.output = output;
		this.day = day;
		this.price = price;
		this.compensation = compensation;
		this.status = status;
	}

	public static Competition of(
		String message,
		Integer day,
		Long price,
		String compensation
	) {
		CompetitionOutput output = CompetitionOutput.from(COMPETITION_DEFAULT_VALUE);
		return new Competition(message, output, day, price, compensation, CompetitionStatus.PENDING);
	}

}
