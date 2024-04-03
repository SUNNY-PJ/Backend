package com.sunny.backend.save.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.PositiveOrZero;

import com.sunny.backend.save.dto.request.SaveRequest;
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
public class Save {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "save_id")
	private Long id;

	@Column
	@PositiveOrZero
	private Long cost;

	@Column
	@FutureOrPresent
	private LocalDate startDate;

	@Column
	@FutureOrPresent
	private LocalDate endDate;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private Users users;

	public void updateSave(SaveRequest saveRequest) {
		this.cost = saveRequest.getCost();
		this.startDate = saveRequest.getStartDate();
		this.endDate = saveRequest.getEndDate();
	}

	public long calculateRemainingDays(Save save) {
		LocalDate currentDate = LocalDate.now();
		return ChronoUnit.DAYS.between(currentDate, save.getEndDate());
	}

	public double calculateSavePercentage(Long userMoney, Save save) {
		if (userMoney == null) {
			return 100.0;
		}
		double percentage = 100.0 - ((userMoney * 100.0) / save.getCost());
		return Math.round(percentage * 10) / 10.0; // 소수점 첫째 자리 반올림
	}

	public boolean checkExpired(LocalDate expirationDate) {
		return expirationDate != null && LocalDate.now().isAfter(expirationDate);
	}
}