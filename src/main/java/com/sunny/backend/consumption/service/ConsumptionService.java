package com.sunny.backend.consumption.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.response.CommonResponse;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumptionService {
	private final ConsumptionRepository consumptionRepository;
	private final ResponseService responseService;
	private final FriendRepository friendRepository;

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<ConsumptionResponse>> createConsumption(
		CustomUserPrincipal customUserPrincipal, ConsumptionRequest consumptionRequest) {
		Users user = customUserPrincipal.getUsers();
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

		return responseService.getSingleResponse(HttpStatus.OK.value(),
			consumptionResponse, "지출을 등록했습니다.");
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
	public ResponseEntity<CommonResponse.ListResponse<ConsumptionResponse>> getConsumptionList(
		CustomUserPrincipal customUserPrincipal) {
		List<Consumption> consumptions = consumptionRepository.findByUsersId(
			customUserPrincipal.getUsers().getId());
		List<ConsumptionResponse> consumptionResponses = ConsumptionResponse.listFrom(consumptions);
		return responseService.getListResponse(HttpStatus.OK.value(),
			consumptionResponses, "지출 내역을 불러왔습니다.");
	}

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<ConsumptionResponse>> updateConsumption(
		CustomUserPrincipal customUserPrincipal,
		ConsumptionRequest consumptionRequest, Long consumptionId) {
		Users user = customUserPrincipal.getUsers();
		Consumption consumption = consumptionRepository.getById(consumptionId);
		Consumption.validateConsumptionByUser(user.getId(), consumption.getUsers().getId());
		consumption.updateConsumption(consumptionRequest);
		ConsumptionResponse consumptionResponse = ConsumptionResponse.from(consumption);
		return responseService.getSingleResponse(HttpStatus.OK.value(),
			consumptionResponse, "지출을 수정했습니다.");
	}

	@Transactional
	public ResponseEntity<CommonResponse.ListResponse<SpendTypeStatisticsResponse>> getSpendTypeStatistics(
		CustomUserPrincipal customUserPrincipal, Integer year, Integer month) {
		Users user = customUserPrincipal.getUsers();
		List<SpendTypeStatisticsResponse> statistics = consumptionRepository.getSpendTypeStatistics(
			user.getId(), year, month);
		return responseService.getListResponse(HttpStatus.OK.value(),
			statistics, "지출 통계 내역을 불러왔습니다.");
	}

	@Transactional
	public ResponseEntity<CommonResponse.ListResponse<ConsumptionResponse.DetailConsumptionResponse>>
	getDetailConsumption(CustomUserPrincipal customUserPrincipal, LocalDate dateField) {
		List<Consumption> detailConsumption =
			consumptionRepository.findByUsersIdAndDateField(customUserPrincipal.getUsers().getId(), dateField);
		List<ConsumptionResponse.DetailConsumptionResponse> detailConsumptions =
			ConsumptionResponse.DetailConsumptionResponse.listFrom(detailConsumption);
		return responseService.getListResponse(HttpStatus.OK.value(),
			detailConsumptions, dateField + "에 맞는 지출 내역을 불러왔습니다.");
	}

	@Transactional
	public ResponseEntity<CommonResponse.GeneralResponse> deleteConsumption(
		CustomUserPrincipal customUserPrincipal, Long consumptionId) {
		Users user = customUserPrincipal.getUsers();
		Consumption consumption = consumptionRepository.getById(consumptionId);
		Consumption.validateConsumptionByUser(user.getId(), consumption.getUsers().getId());
		consumptionRepository.deleteById(consumptionId);
		return responseService.getGeneralResponse(HttpStatus.OK.value(),
			"지출 내역을 삭제했습니다.");
	}

	@Transactional
	public ResponseEntity<CommonResponse.ListResponse<ConsumptionResponse.DetailConsumptionResponse>>
	getConsumptionByCategory(CustomUserPrincipal customUserPrincipal, SpendType spendType,
		Integer year, Integer month) {
		List<ConsumptionResponse.DetailConsumptionResponse> detailConsumptions =
			consumptionRepository.getConsumptionByCategory(customUserPrincipal.getUsers().getId(), spendType, year,
				month);
		return responseService.getListResponse(HttpStatus.OK.value(),
			detailConsumptions, spendType + "에 맞는 지출 내역을 불러왔습니다.");
	}
}
