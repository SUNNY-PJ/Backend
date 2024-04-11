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
import com.sunny.backend.save.domain.Save;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.repository.UserRepository;
import com.sunny.backend.util.MathUtil;
import com.sunny.backend.util.SockMessageUtil;

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
	private final SockMessageUtil sockMessageUtil;

	@Transactional
	public ConsumptionResponse createConsumption(
		CustomUserPrincipal customUserPrincipal,
		ConsumptionRequest consumptionRequest
	) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		Consumption consumption = Consumption.of(
			consumptionRequest.getCategory(),
			consumptionRequest.getName(),
			consumptionRequest.getMoney(),
			consumptionRequest.getDateField(),
			user
		);
		consumptionRepository.save(consumption);

		reflectCompetitionResultIfCompeting(user);

		checkSaveAndSendMessage(user);

		return ConsumptionResponse.from(consumption);
	}

	public void reflectCompetitionResultIfCompeting(Users user) {
		for (Friend friend : friendRepository.findByUsersAndCompetitionIsNotNullAndCompetition_Status(user,
			CompetitionStatus.PROCEEDING)) {
			Competition competition = friend.getCompetition();
			Users userFriend = friend.getUserFriend();

			Long userId = user.getId();
			Long userFriendId = userFriend.getId();
			LocalDate startDate = competition.getStartDate();
			LocalDate endDate = competition.getEndDate();
			Long userUsedMoney = consumptionRepository.getComsumptionMoney(userId, startDate, endDate);
			Long friendUsedMoney = consumptionRepository.getComsumptionMoney(userFriendId, startDate, endDate);

			double percentageUsed = MathUtil.calculatePercentage(userUsedMoney, competition.getPrice());
			double friendsPercentageUsed = MathUtil.calculatePercentage(friendUsedMoney, competition.getPrice());

			competition.updateOutput(percentageUsed, friendsPercentageUsed, userId, userFriendId);

			if (friendsPercentageUsed <= 0) {
				competition.updateStatus(CompetitionStatus.COMPLETE);
				sockMessageUtil.sendCompetitionUserWinner(user, userFriend, competition);
			} else if (percentageUsed <= 0) {
				competition.updateStatus(CompetitionStatus.COMPLETE);
				sockMessageUtil.sendCompetitionUserFriendWinner(user, userFriend, competition);
			}
		}
	}

	public void checkSaveAndSendMessage(Users users) {
		if (users.isExistLastSave()) {
			Save save = users.getLastSaveOrException();
			Long userUsedMoney = consumptionRepository.getComsumptionMoney(users.getId(), save.getStartDate(),
				save.getEndDate());
			double percentage = MathUtil.calculatePercentage(userUsedMoney, save.getCost());
			sockMessageUtil.sendWaringSavingGoal(percentage, users, save);
		}
	}

	@Transactional
	public ResponseEntity<CommonResponse.ListResponse<ConsumptionResponse>> getConsumptionList(
		CustomUserPrincipal customUserPrincipal, Long consumptionId) {
		Users user = userRepository.getById(customUserPrincipal.getId());

		List<Consumption> consumptions = consumptionRepository.getConsumption(user.getId(), consumptionId);
		List<ConsumptionResponse> consumptionResponses = ConsumptionResponse.listFrom(consumptions);

		return responseService.getListResponse(HttpStatus.OK.value(),
			consumptionResponses, "지출 내역을 불러왔습니다.");
	}

	@Transactional
	public void updateConsumption(
		CustomUserPrincipal customUserPrincipal,
		ConsumptionRequest consumptionRequest,
		Long consumptionId
	) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		Consumption consumption = consumptionRepository.getById(consumptionId);
		Consumption.validateConsumptionByUser(user.getId(), consumption.getUsers().getId());
		consumption.updateConsumption(consumptionRequest);
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
	public void deleteConsumption(
		CustomUserPrincipal customUserPrincipal,
		Long consumptionId
	) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		Consumption consumption = consumptionRepository.getById(consumptionId);
		Consumption.validateConsumptionByUser(user.getId(), consumption.getUsers().getId());
		consumptionRepository.deleteById(consumptionId);
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
