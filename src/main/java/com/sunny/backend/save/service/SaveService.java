package com.sunny.backend.save.service;

import com.sunny.backend.competition.domain.Competition;
import com.sunny.backend.consumption.domain.Consumption;
import com.sunny.backend.save.dto.response.SaveResponse.SaveListResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sunny.backend.auth.jwt.CustomUserPrincipal;
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
@RequiredArgsConstructor
@Slf4j
public class SaveService {

	private final SaveRepository saveRepository;
	private final ResponseService responseService;
	private final ConsumptionRepository consumptionRepository;

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<SaveResponse>> createSaveGoal(
			CustomUserPrincipal customUserPrincipal, SaveRequest saveRequest) {
		System.out.println("호출");
		Users user = customUserPrincipal.getUsers();
		List<Save> saves = saveRepository.findAllByUsers_Id(user.getId());
		System.out.println(saveRequest.getStartDate());

		boolean allSavesExpired = saves.stream().allMatch(save -> save.checkExpired(save.getEndDate()));
		if (allSavesExpired) {
			Save save = Save.builder()
					.cost(saveRequest.getCost())
					.startDate(saveRequest.getStartDate())
					.endDate(saveRequest.getEndDate())
					.users(user)
					.build();
			saveRepository.save(save);
			user.addSave(save);
			return responseService.getSingleResponse(HttpStatus.OK.value(), SaveResponse.from(save,true),
					"절약 목표를 등록했습니다.");
		} else {
			if (!saves.isEmpty()) {
				Save lastSave = saves.get(saves.size() - 1);
				SaveResponse saveResponse = SaveResponse.from(lastSave,checkSuccessed(user,lastSave));
				return responseService.getSingleResponse(HttpStatus.OK.value(), saveResponse, "이미 등록된 절약 목표가 존재합니다.");
			} else {
				return responseService.getSingleResponse(HttpStatus.OK.value(), null, "등록된 절약 목표를 찾을 수 없습니다.");
			}
		}
	}
	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<SaveResponse>> updateSaveGoal(
		CustomUserPrincipal customUserPrincipal, SaveRequest saveRequest) {
		Users user = customUserPrincipal.getUsers();
		List<Save> saves = saveRepository.findAllByUsers_Id(user.getId());
		Save lastSave = saves.get(saves.size() - 1);
		lastSave.updateSave(saveRequest);
		boolean success=checkSuccessed(user,lastSave);
		return responseService.getSingleResponse(HttpStatus.OK.value(), SaveResponse.from(lastSave,success), "절약 목표를 수정했습니다.");
	}

	@Transactional
	public ResponseEntity<CommonResponse.ListResponse<SaveResponse.DetailSaveResponse>> getSaveGoal(
			CustomUserPrincipal customUserPrincipal) {
		Users user = customUserPrincipal.getUsers();
		List<Save> saves = saveRepository.findAllByUsers_Id(user.getId());

		List<SaveResponse.DetailSaveResponse> saveResponses = saves.stream()
				.map(save -> {
					long remainingDays = save.calculateRemainingDays(save);
					Long userMoney = consumptionRepository.getComsumptionMoney(user.getId(), save.getStartDate(), save.getEndDate());
					log.info( "userMoney:"+userMoney);
					log.info( "save:"+save.getCost());
					double percentageUsed = save.calculateSavePercentage(userMoney, save);
					return SaveResponse.DetailSaveResponse.of(remainingDays, percentageUsed, save.getCost());
				})
				.toList();
		return responseService.getListResponse(HttpStatus.OK.value(), saveResponses,
				"절약 목표를 성공적으로 조회했습니다.");
	}
	public ResponseEntity<CommonResponse.ListResponse<SaveListResponse>> getDetailSaveGoal(
			CustomUserPrincipal customUserPrincipal) {
		Users user = customUserPrincipal.getUsers();
		List<Save> saves = saveRepository.findAllByUsers_Id(user.getId());
		List<SaveListResponse> saveResponses = saves.stream().map(save -> {
			return SaveListResponse.from(save, checkSuccessed(user, save));
		}).toList();
		return responseService.getListResponse(HttpStatus.OK.value(), saveResponses,
				"절약 목표 성공적으로 조회했습니다.");
	}

	public boolean checkSuccessed(Users user,Save save) {
		Long userMoney = consumptionRepository.getComsumptionMoney(user.getId(), save.getStartDate(), save.getEndDate());
		double percentageUsed = save.calculateSavePercentage(userMoney,save);
		return percentageUsed >= 0;
	}

	//TODO 메소드 분리
	private double calculateUserPercentage( Long userId, Save save) {
		if (save == null) {
			return 100.0;
		}

		// 사용자 소비 금액 계산
		Long totalSpent = consumptionRepository.getComsumptionMoney(userId, save.getStartDate(), save.getEndDate());

		//소비 비율 계산
		double percentage = 100.0 - ((totalSpent / save.getCost()) * 100.0);
		return Math.round(percentage * 10) / 10.0; // 소수점 첫째 자리 반올림
	}

}
