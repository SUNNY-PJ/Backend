package com.sunny.backend.save.service;

import static com.sunny.backend.save.exception.SaveErrorCode.*;

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
import com.sunny.backend.save.dto.response.SaveResponse;
import com.sunny.backend.save.repository.SaveRepository;
import com.sunny.backend.user.domain.Users;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SaveService {
	private final SaveRepository saveRepository;
	private final ResponseService responseService;
	private final ConsumptionRepository consumptionRepository;

	public ResponseEntity<CommonResponse.SingleResponse<SaveResponse>> createSaveGoal(
		CustomUserPrincipal customUserPrincipal, SaveRequest saveRequest) {
		Users user = customUserPrincipal.getUsers();
		Save save = Save.builder()
			.cost(saveRequest.getCost())
			.startDate(saveRequest.getStartDate())
			.endDate(saveRequest.getEndDate())
			.users(user)
			.build();
		saveRepository.save(save);
		user.addSave(save);

		return responseService.getSingleResponse(HttpStatus.OK.value(), SaveResponse.from(save), "절약 목표를 등록했습니다.");
	}

	public ResponseEntity<CommonResponse.SingleResponse<SaveResponse>> updateSaveGoal(
		CustomUserPrincipal customUserPrincipal, SaveRequest saveRequest) {
		Users user = customUserPrincipal.getUsers();
		Save save = saveRepository.findByUsers_Id(user.getId())
			.orElseThrow(() -> new CustomException(SAVE_NOT_FOUND));
		save.updateSave(saveRequest);
		return responseService.getSingleResponse(HttpStatus.OK.value(), SaveResponse.from(save), "절약 목표를 수정했습니다.");
	}

	public ResponseEntity<CommonResponse.SingleResponse<SaveResponse.DetailSaveResponse>> getDetailSaveGoal(
		CustomUserPrincipal customUserPrincipal) {
		Users user = customUserPrincipal.getUsers();
		Save save = saveRepository.findByUsers_Id(user.getId())
			.orElseThrow(() -> new CustomException(SAVE_NOT_FOUND));

		long remainingDays = save.calculateRemainingDays(save);
		Long userMoney = consumptionRepository.getComsumptionMoney(user.getId(), save.getStartDate(),
			save.getEndDate());
		double percentageUsed = save.calculateSavePercentage(userMoney, save);
		SaveResponse.DetailSaveResponse saveResponse = SaveResponse.DetailSaveResponse.of(remainingDays,
			percentageUsed);
		return responseService.getSingleResponse(HttpStatus.OK.value(), saveResponse,
			"절약 목표를 성공적으로 조회했습니다.");
	}

}
