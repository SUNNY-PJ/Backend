package com.sunny.backend.save.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.consumption.repository.ConsumptionRepository;
import com.sunny.backend.save.domain.Save;
import com.sunny.backend.save.dto.request.SaveRequest;
import com.sunny.backend.save.dto.response.DetailSaveResponse;
import com.sunny.backend.save.dto.response.SaveResponse;
import com.sunny.backend.save.dto.response.SaveResponses;
import com.sunny.backend.save.repository.SaveRepository;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaveService {
	private final UserRepository userRepository;
	private final SaveRepository saveRepository;
	private final ConsumptionRepository consumptionRepository;

	@Transactional
	public SaveResponse createSaveGoal(
		CustomUserPrincipal customUserPrincipal,
		SaveRequest saveRequest
	) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		// List<Save> saves = saveRepository.findAllByUsers_Id(user.getId());
		// boolean allSavesExpired = saves.stream().allMatch(save -> save.checkExpired(save.getEndDate()));
		user.checkExpiredSave();

		Save save = Save.builder()
			.cost(saveRequest.getCost())
			.startDate(saveRequest.getStartDate())
			.endDate(saveRequest.getEndDate())
			.users(user)
			.build();
		saveRepository.save(save);
		user.addSave(save);
		return SaveResponse.from(save, true);
		//
		// if (allSavesExpired) {
		// 	Save save = Save.builder()
		// 		.cost(saveRequest.getCost())
		// 		.startDate(saveRequest.getStartDate())
		// 		.endDate(saveRequest.getEndDate())
		// 		.users(user)
		// 		.build();
		// 	saveRepository.save(save);
		// 	user.addSave(save);
		// 	return responseService.getSingleResponse(HttpStatus.OK.value(), SaveResponse.from(save, true),
		// 		"절약 목표를 등록했습니다.");
		// } else {
		// 	if (!saves.isEmpty()) {
		// 		Save lastSave = saves.get(saves.size() - 1);
		// 		SaveResponse saveResponse = SaveResponse.from(lastSave, checkSuccessed(user, lastSave));
		// 		throw new CustomException(SaveErrorCode.ALREADY_SAVE);
		// 	} else {
		// 		return responseService.getSingleResponse(HttpStatus.OK.value(), null, "등록된 절약 목표를 찾을 수 없습니다.");
		// 	}
		// }
	}

	@Transactional
	public SaveResponse updateSaveGoal(CustomUserPrincipal customUserPrincipal, SaveRequest saveRequest) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		List<Save> saves = saveRepository.findAllByUsers_Id(user.getId());
		Save lastSave = saves.get(saves.size() - 1);
		lastSave.updateSave(saveRequest);
		boolean success = checkSuccessed(user, lastSave);
		return SaveResponse.from(lastSave, success);
	}

	@Transactional
	public List<DetailSaveResponse> getSaveGoal(
		CustomUserPrincipal customUserPrincipal) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		List<Save> saves = saveRepository.findAllByUsers_Id(user.getId());

		return saves.stream()
			.map(save -> {
				long remainingDays = save.calculateRemainingDays(save);
				Long userMoney = consumptionRepository.getComsumptionMoney(user.getId(), save.getStartDate(),
					save.getEndDate());
				double percentageUsed = save.calculateSavePercentage(userMoney, save);
				return DetailSaveResponse.of(remainingDays, percentageUsed, save.getCost());
			})
			.toList();
	}

	public List<SaveResponses> getDetailSaveGoal(
		CustomUserPrincipal customUserPrincipal) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		List<Save> saves = saveRepository.findAllByUsers_Id(user.getId());
		return saves.stream()
			.map(save -> SaveResponses.from(save, checkSuccessed(user, save)))
			.toList();
	}

	public boolean checkSuccessed(Users user, Save save) {
		Long userMoney = consumptionRepository.getComsumptionMoney(user.getId(), save.getStartDate(),
			save.getEndDate());
		double percentageUsed = save.calculateSavePercentage(userMoney, save);
		return percentageUsed >= 0;
	}
}
