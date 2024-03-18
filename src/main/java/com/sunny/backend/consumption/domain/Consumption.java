package com.sunny.backend.consumption.domain;

import static com.sunny.backend.common.CommonErrorCode.*;

import java.time.LocalDate;

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
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.PositiveOrZero;

import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.consumption.dto.request.ConsumptionRequest;
import com.sunny.backend.user.domain.Users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Consumption {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	@Enumerated(value = EnumType.STRING)
	private SpendType category;

	@Column
	private String name;

	@Column
	@PositiveOrZero
	private Long money;

	@Column
	@PastOrPresent
	private LocalDate dateField;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "users_id")
	private Users users;

	public void updateConsumption(ConsumptionRequest consumptionRequest) {
		this.category = consumptionRequest.getCategory();
		this.name = consumptionRequest.getName();
		this.money = consumptionRequest.getMoney();
		this.dateField = consumptionRequest.getDateField();
	}

	public static void validateConsumptionByUser(Long userId, Long consumptionUserId) {
		if (!userId.equals(consumptionUserId)) {
			throw new CustomException(NO_USER_PERMISSION);
		}
	}
    
}