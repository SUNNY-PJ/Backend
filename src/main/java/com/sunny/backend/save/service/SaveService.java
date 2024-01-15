package com.sunny.backend.save.service;


import com.sunny.backend.consumption.repository.ConsumptionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.dto.request.save.SaveRequest;
import com.sunny.backend.dto.response.save.SaveResponse;
import com.sunny.backend.save.domain.Save;
import com.sunny.backend.save.repository.SaveRepository;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.user.Users;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SaveService {

	private final SaveRepository saveRepository;
	private final ResponseService responseService;
	private final ConsumptionRepository consumptionRepository;

	public ResponseEntity<CommonResponse.SingleResponse<SaveResponse>> createSaveGoal(
			CustomUserPrincipal customUserPrincipal,
			SaveRequest saveRequest) {
		Users user = customUserPrincipal.getUsers();
		Save save = Save.builder()
				.cost(saveRequest.getCost())
				.startDate(saveRequest.getStartDate())
				.endDate(saveRequest.getEndDate())
				.users(user)
				.build();
		user.addSave(save);
		saveRepository.save(save);
		user.addSave(save);
		SaveResponse saveResponse = SaveResponse.from(save);
		return responseService.getSingleResponse(HttpStatus.OK.value(), saveResponse, "절약 목표를 등록했습니다.");
	}

	public ResponseEntity<CommonResponse.SingleResponse<SaveResponse>> updateSaveGoal(
			CustomUserPrincipal customUserPrincipal, SaveRequest saveRequest) {
		Users user = customUserPrincipal.getUsers();
		Save save = saveRepository.findByUsers_Id(user.getId());
		save.updateSave(saveRequest);
		SaveResponse saveResponse = SaveResponse.from(save);
		return responseService.getSingleResponse(HttpStatus.OK.value(), saveResponse, "절약 목표를 수정했습니다.");
	}

	public ResponseEntity<CommonResponse.SingleResponse<SaveResponse>> getSaveGoal(
			CustomUserPrincipal customUserPrincipal) {
		Users user = customUserPrincipal.getUsers();
		Save save = saveRepository.findByUsers_Id(user.getId());
		SaveResponse saveResponse = SaveResponse.from(save);
		return responseService.getSingleResponse(HttpStatus.OK.value(), saveResponse,
				"절약 목표를 성공적으로 조회했습니다.");
	}

	public ResponseEntity<CommonResponse.SingleResponse<SaveResponse.DetailSaveResponse>>
	getDetailSaveGoal(CustomUserPrincipal customUserPrincipal) {
		Users user = customUserPrincipal.getUsers();
		Save save = saveRepository.findByUsers_Id(user.getId());
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
