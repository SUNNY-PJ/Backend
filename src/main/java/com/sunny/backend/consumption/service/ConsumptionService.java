package com.sunny.backend.consumption.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.competition.domain.Competition;
import com.sunny.backend.competition.domain.CompetitionStatus;
import com.sunny.backend.consumption.domain.Consumption;
import com.sunny.backend.consumption.domain.SpendType;
import com.sunny.backend.consumption.dto.request.ConsumptionRequest;
import com.sunny.backend.consumption.dto.response.ConsumptionResponse;
import com.sunny.backend.consumption.dto.response.SpendTypeStatisticsResponse;
import com.sunny.backend.consumption.repository.ConsumptionRepository;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.repository.FriendRepository;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumptionService {
	private final ConsumptionRepository consumptionRepository;
	private final ResponseService responseService;
	private final FriendRepository friendRepository;
	private final UserRepository userRepository;

	@Transactional
	public ConsumptionResponse createConsumption(
		CustomUserPrincipal customUserPrincipal, ConsumptionRequest consumptionRequest) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		Consumption consumption = Consumption.builder()
			.name(consumptionRequest.getName())
			.category(consumptionRequest.getCategory())
			.money(consumptionRequest.getMoney())
			.dateField(consumptionRequest.getDateField())
			.users(user)
			.build();
		consumptionRepository.save(consumption);
		// TODO 로직 맞는지 확인 부탁
		if (user.getConsumptionList() == null) {
			user.addConsumption(consumption);
		}
		ConsumptionResponse consumptionResponse = ConsumptionResponse.from(consumption);

		for (Friend friend : friendRepository.findByUsersAndCompetitionIsNotNullAndCompetition_Status(user,
			CompetitionStatus.PROCEEDING)) {
			double percentageUsed = calculateUserPercentage(user.getId(), friend.getCompetition());
			double friendsPercentageUsed = calculateUserPercentage(friend.getUserFriend().getId(),
				friend.getCompetition());

			friend.getCompetition()
				.getOutput()
				.updateOutput(percentageUsed, friendsPercentageUsed, user.getId(), friend.getUserFriend().getId());
		}

		return consumptionResponse;
	}

	public double calculateUserPercentage(Long userId, Competition competition) {
		Long totalSpent = consumptionRepository.getComsumptionMoney(userId, competition.getStartDate(),
			competition.getEndDate());
		if (totalSpent == null) {
			return 100.0;
		}

		double percentage = 100.0 - ((totalSpent * 100.0) / competition.getPrice());
		return Math.round(percentage * 10) / 10.0; // 소수점 첫째 자리 반올림
	}

	@Transactional
	public List<ConsumptionResponse> getConsumptionList(CustomUserPrincipal customUserPrincipal) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		List<Consumption> consumptions = consumptionRepository.findByUsersId(user.getId());
		return ConsumptionResponse.listFrom(consumptions);
	}

	@Transactional
	public ConsumptionResponse updateConsumption(
		CustomUserPrincipal customUserPrincipal,
		ConsumptionRequest consumptionRequest,
		Long consumptionId
	) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		Consumption consumption = consumptionRepository.getById(consumptionId);
		Consumption.validateConsumptionByUser(user.getId(), consumption.getUsers().getId());
		consumption.updateConsumption(consumptionRequest);
		return ConsumptionResponse.from(consumption);
	}

	@Transactional
	public List<SpendTypeStatisticsResponse> getSpendTypeStatistics(
		CustomUserPrincipal customUserPrincipal, Integer year, Integer month) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		return consumptionRepository.getSpendTypeStatistics(user.getId(), year, month);
	}

	@Transactional
	public List<ConsumptionResponse.DetailConsumptionResponse> getDetailConsumption(
		CustomUserPrincipal customUserPrincipal, LocalDate dateField) {
		List<Consumption> detailConsumption =
			consumptionRepository.findByUsersIdAndDateField(customUserPrincipal.getId(), dateField);
		return ConsumptionResponse.DetailConsumptionResponse.listFrom(detailConsumption);
	}

	@Transactional
	public void deleteConsumption(CustomUserPrincipal customUserPrincipal, Long consumptionId) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		Consumption consumption = consumptionRepository.getById(consumptionId);
		Consumption.validateConsumptionByUser(user.getId(), consumption.getUsers().getId());
		consumptionRepository.deleteById(consumptionId);
	}

	@Transactional
	public List<ConsumptionResponse.DetailConsumptionResponse> getConsumptionByCategory(
		CustomUserPrincipal customUserPrincipal,
		SpendType spendType,
		Integer year,
		Integer month
	) {
		return consumptionRepository.getConsumptionByCategory(customUserPrincipal.getId(), spendType, year, month);
	}
}
