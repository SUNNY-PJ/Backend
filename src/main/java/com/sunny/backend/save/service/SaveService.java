package com.sunny.backend.save.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.consumption.repository.ConsumptionRepository;
import com.sunny.backend.save.domain.Save;
import com.sunny.backend.save.dto.request.SaveRequest;
import com.sunny.backend.save.dto.response.DetailSaveResponse;
import com.sunny.backend.save.dto.response.SaveResponse;
import com.sunny.backend.save.exception.SaveErrorCode;
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
	private final ResponseService responseService;
	private final ConsumptionRepository consumptionRepository;

	@Transactional
	public Long createSaveGoal(
		CustomUserPrincipal customUserPrincipal,
		SaveRequest saveRequest
	) {
		Users user = userRepository.getById(customUserPrincipal.getId());

		if (!user.getSaves().isEmpty()) {
			Save save = user.getLastSaveOrException();
			if (save.isValidSave()) {
				throw new CustomException(SaveErrorCode.SAVE_ALREADY);
			}
		}
		Save newSave = createSave(saveRequest, user);
		return newSave.getId();
	}

	public Save createSave(SaveRequest saveRequest, Users users) {
		Save newSave = Save.of(
			saveRequest.getCost(),
			saveRequest.getStartDate(),
			saveRequest.getEndDate(),
			users
		);
		users.addSave(newSave);
		return saveRepository.save(newSave);
	}

	@Transactional
	public void updateSaveGoal(
		CustomUserPrincipal customUserPrincipal,
		SaveRequest saveRequest
	) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		Save save = user.getLastSaveOrException();
		save.updateSave(saveRequest);
	}

	@Transactional
	public List<DetailSaveResponse> getSaveGoal(CustomUserPrincipal customUserPrincipal) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		return user.getSaves().stream()
			.map(save -> {
				long remainingDays = save.calculateRemainingDays(save);
				Long userMoney = consumptionRepository.getComsumptionMoney(user.getId(), save.getStartDate(),
					save.getEndDate());
				double percentageUsed = save.calculateSavePercentage(userMoney, save);
				return DetailSaveResponse.of(save.getId(), remainingDays, percentageUsed, save.getCost());
			})
			.toList();
	}

	//TODO 삭제 예정
	public ResponseEntity<CommonResponse.ListResponse<SaveResponse>> getDetailSaveGoal(
		CustomUserPrincipal customUserPrincipal) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		List<Save> saves = user.getSaves();
		List<SaveResponse> saveResponses = saves.stream()
			.map(SaveResponse::from)
			.toList();
		return responseService.getListResponse(HttpStatus.OK.value(), saveResponses,
			"절약 목표 성공적으로 조회했습니다.");
	}

	public SaveResponse getDetailSave(CustomUserPrincipal customUserPrincipal, Long saveId) {
		Save save = saveRepository.getById(saveId);
		return SaveResponse.from(save);
	}

}
