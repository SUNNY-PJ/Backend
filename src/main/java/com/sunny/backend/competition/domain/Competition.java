package com.sunny.backend.competition.domain;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.competition.exception.CompetitionErrorCode;
import com.sunny.backend.friends.domain.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Competition {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; //경쟁 id

	@Column
	private String message; // 도발 메세지

	@Column
	private Long output; // 결과

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

	@Column
	@Enumerated(value = EnumType.STRING)
	private Status status;

	public void approveStatus() {
		status = Status.APPROVE;
	}

	public void updateOutput(Long output) {
		this.output = output;
	}

	public void validateStatus() {
		if (status.equals(Status.WAIT)) {
			throw new CustomException(CompetitionErrorCode.COMPETITION_NOT_APPROVE);
		}
		if (status.equals(Status.APPROVE)) {
			throw new CustomException(CompetitionErrorCode.COMPETITION_EXIST);
		}
	}

	public void addDate(LocalDate startDate, LocalDate endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}
}
