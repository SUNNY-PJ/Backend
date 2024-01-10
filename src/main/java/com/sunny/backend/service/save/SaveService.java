package com.sunny.backend.service.save;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.dto.request.save.SaveRequest;
import com.sunny.backend.dto.response.save.SaveResponse;
import com.sunny.backend.entity.Save;
import com.sunny.backend.repository.save.SaveRepository;
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

	public ResponseEntity<CommonResponse.SingleResponse<SaveResponse>> createSaveGoal(
		CustomUserPrincipal customUserPrincipal,
		SaveRequest saveRequest) {
		Users user = customUserPrincipal.getUsers();
		Save save = Save.builder()
				.cost(saveRequest.getCost())
				.saveContent(saveRequest.getSaveContent())
				.startDate(saveRequest.getStartDate())
				.endDate(saveRequest.getEndDate())
				.users(user)
				.build();
		saveRepository.save(save);
		user.addSave(save);
		SaveResponse saveResponse = SaveResponse.from(save);
		return responseService.getSingleResponse(HttpStatus.OK.value(), saveResponse, "절약 목표를 등록했습니다.");

	}

	//절약 목표 수정
	public ResponseEntity<CommonResponse.SingleResponse<SaveResponse>> updateSaveGoal(
		CustomUserPrincipal customUserPrincipal,
		Long savedId,
		SaveRequest saveRequest) {
		Users user = customUserPrincipal.getUsers();
		// To do : save 명칭 변경
		Save save = saveRepository.getById(savedId);
		save.updateSave(saveRequest);
		SaveResponse saveResponse = SaveResponse.from(save);
		return responseService.getSingleResponse(HttpStatus.OK.value(), saveResponse, "절약 목표를 수정했습니다.");
	}

	public ResponseEntity<CommonResponse.SingleResponse<SaveResponse>> getSaveGoal(
		CustomUserPrincipal customUserPrincipal,
		Long savedId) {
		Users user = customUserPrincipal.getUsers();
		// To do : save 명칭 변경
		Save save = saveRepository.getById(savedId);
		SaveResponse saveResponse = SaveResponse.from(save);
		return responseService.getSingleResponse(HttpStatus.OK.value(), saveResponse,
				"절약 목표를 성공적으로 조회했습니다.");
	}

	//절약 현황 (현재까지의 지출 금액 / 목표 금액)
    /*
    사용자는 절약 목표 알림을 받을 수 있다.
    [절약 목표 금액 < 소비 금액]
    -소비 금액이 절약 목표 금액 보다 근접&초과(기준 : 30% , 60%, 80%) 시 앱 자체에서 알림을 준다.
    **/

}
