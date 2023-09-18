package com.sunny.backend.service.save;

import com.amazonaws.services.kms.model.NotFoundException;
import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.dto.request.save.SaveRequest;
import com.sunny.backend.dto.response.save.SaveResponse;
import com.sunny.backend.entity.Save;
import com.sunny.backend.repository.save.SaveRepository;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.user.Users;
import com.sunny.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaveService {
    private final SaveRepository saveRepository;
    private final UserRepository userRepository;
    private final ResponseService responseService;

    //절약 목표 등록
    public CommonResponse.SingleResponse createSaveGoal(CustomUserPrincipal customUserPrincipal, SaveRequest saveRequest) {
        Users user = customUserPrincipal.getUsers();
        // To do : save 명칭 변경
        Save saveGoal= Save.builder()
                .cost(saveRequest.getCost())
                .startDate(saveRequest.getStartDate())
                .endDate(saveRequest.getEndDate())
                .users(user)
                .build();
        saveRepository.save(saveGoal);
        user.setSave(saveGoal);
        return responseService.getSingleResponse(HttpStatus.OK.value(), new SaveResponse(saveGoal), "절약 목표를 등록했습니다.");

    }

    //절약 목표 수정
    public CommonResponse.SingleResponse updateSaveGoal(CustomUserPrincipal customUserPrincipal, Long savedId,SaveRequest saveRequest) {
        Users user = customUserPrincipal.getUsers();
        // To do : save 명칭 변경
        Save save = saveRepository.findById(savedId).orElseThrow(() -> new NotFoundException("could not found save goal"));

        save.updateSave(saveRequest);
        //To do -> Transactional , save()에 대해
        saveRepository.save(save); //update시에는 sava 필요 없음 ?
        return responseService.getSingleResponse(HttpStatus.OK.value(), new SaveResponse(save), "절약 목표를 수정했습니다.");

    }

    //절약 현황 (현재까지의 지출 금액 / 목표 금액)
    /*
    사용자는 절약 목표 알림을 받을 수 있다.
    [절약 목표 금액 < 소비 금액]
    -소비 금액이 절약 목표 금액 보다 근접&초과(기준 : 50% , 90%, 100%) 시 앱 자체에서 알림을 준다.
    **/


}
