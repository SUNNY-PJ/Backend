package com.sunny.backend.consumption.service;

import static com.sunny.backend.common.ComnConstant.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
import com.sunny.backend.consumption.dto.response.SaveGoalAlertResponse;
import com.sunny.backend.consumption.dto.response.SpendTypeStatisticsResponse;
import com.sunny.backend.consumption.repository.ConsumptionRepository;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.repository.FriendRepository;
import com.sunny.backend.save.domain.Save;
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
	private final SimpMessagingTemplate template;

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<ConsumptionResponse>> createConsumption(
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
		user.addConsumption(consumption);
		
		ConsumptionResponse consumptionResponse = ConsumptionResponse.from(consumption);

		for (Friend friend : friendRepository.findByUsersAndCompetitionIsNotNullAndCompetition_Status(user,
			CompetitionStatus.PROCEEDING)) {
			Competition competition = friend.getCompetition();
			double percentageUsed = calculateUserPercentage(user.getId(), competition.getStartDate(),
				competition.getEndDate(), competition.getPrice());
			double friendsPercentageUsed = calculateUserPercentage(friend.getUserFriend().getId(),
				competition.getStartDate(), competition.getEndDate(), competition.getPrice());

			friend.getCompetition()
				.getOutput()
				.updateOutput(percentageUsed, friendsPercentageUsed, user.getId(), friend.getUserFriend().getId());
		}

		if (user.isExistLastSave()) {
			Save save = user.getLastSaveOrException();
			double percentage = calculateUserPercentage(user.getId(), save.getStartDate(), save.getEndDate(),
				save.getCost());
			if (percentage <= 80) {
				String message;
				if (percentage <= 0) {
					message = SAVE_MESSAGE_BELOW_0;
				} else if (percentage <= 20) {
					message = SAVE_MESSAGE_BELOW_20;
				} else if (percentage <= 50) {
					message = SAVE_MESSAGE_BELOW_50;
				} else {
					message = SAVE_MESSAGE_BELOW_80;
				}
				message = String.format("목표금액 %d원까지 %.2f%% 남았어요! %s", save.getCost(), percentage, message);
				template.convertAndSend("/sub/user/" + user.getId(),
					new SaveGoalAlertResponse(save.getCost(), percentage, message));
			}
		}

		return responseService.getSingleResponse(HttpStatus.OK.value(),
			consumptionResponse, "지출을 등록했습니다.");
	}

	public double calculateUserPercentage(Long userId, LocalDate startDate, LocalDate endDate, Long price) {
		Long totalSpent = consumptionRepository.getComsumptionMoney(userId, startDate, endDate);
		if (totalSpent == null) {
			return 100.0;
		}

		double percentage = 100.0 - ((totalSpent * 100.0) / price);
		return Math.round(percentage * 10) / 10.0; // 소수점 첫째 자리 반올림
	}

	@Transactional
	public ResponseEntity<CommonResponse.ListResponse<ConsumptionResponse>> getConsumptionList(
		CustomUserPrincipal customUserPrincipal) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		List<Consumption> consumptions = consumptionRepository.findByUsersId(user.getId());
		List<ConsumptionResponse> consumptionResponses = ConsumptionResponse.listFrom(consumptions);
		return responseService.getListResponse(HttpStatus.OK.value(),
			consumptionResponses, "지출 내역을 불러왔습니다.");
	}

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<ConsumptionResponse>> updateConsumption(
		CustomUserPrincipal customUserPrincipal,
		ConsumptionRequest consumptionRequest, Long consumptionId) {
		Users user = userRepository.getById(customUserPrincipal.getId());
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
		Users user = userRepository.getById(customUserPrincipal.getId());
		List<SpendTypeStatisticsResponse> statistics = consumptionRepository.getSpendTypeStatistics(
			user.getId(), year, month);
		return responseService.getListResponse(HttpStatus.OK.value(),
			statistics, "지출 통계 내역을 불러왔습니다.");
	}

	@Transactional
	public ResponseEntity<CommonResponse.ListResponse<ConsumptionResponse.DetailConsumptionResponse>>
	getDetailConsumption(CustomUserPrincipal customUserPrincipal, LocalDate dateField) {
		List<Consumption> detailConsumption =
			consumptionRepository.findByUsersIdAndDateField(customUserPrincipal.getId(), dateField);
		List<ConsumptionResponse.DetailConsumptionResponse> detailConsumptions =
			ConsumptionResponse.DetailConsumptionResponse.listFrom(detailConsumption);
		return responseService.getListResponse(HttpStatus.OK.value(),
			detailConsumptions, dateField + "에 맞는 지출 내역을 불러왔습니다.");
	}

	@Transactional
	public ResponseEntity<CommonResponse.GeneralResponse> deleteConsumption(
		CustomUserPrincipal customUserPrincipal, Long consumptionId) {
		Users user = userRepository.getById(customUserPrincipal.getId());
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
			consumptionRepository.getConsumptionByCategory(customUserPrincipal.getId(), spendType, year, month);
		return responseService.getListResponse(HttpStatus.OK.value(),
			detailConsumptions, spendType + "에 맞는 지출 내역을 불러왔습니다.");
	}
}
