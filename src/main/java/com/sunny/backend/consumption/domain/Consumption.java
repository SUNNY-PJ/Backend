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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

	public Consumption(SpendType category, String name, Long money, LocalDate dateField, Users users) {
		this.category = category;
		this.name = name;
		this.money = money;
		this.dateField = dateField;
		this.users = users;
	}

	public static Consumption of(SpendType category, String name, Long money, LocalDate dateField, Users users) {
		Consumption consumption = new Consumption(category, name, money, dateField, users);
		users.addConsumption(consumption);
		return consumption;
	}

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