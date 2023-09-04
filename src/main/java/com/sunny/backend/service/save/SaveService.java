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
    public CommonResponse createSaveGoal(CustomUserPrincipal customUserPrincipal, SaveRequest saveRequest) {
        Users user=userRepository.findById(customUserPrincipal.getId())
                .orElseThrow(()->new NotFoundException("could not found user!"));
        // To do : save 명칭 변경
        Save saveGoal= Save.builder()
                .cost(saveRequest.getCost())
                .startDate(saveRequest.getStartDate())
                .endDate(saveRequest.getEndDate())
                .user(user)
                .build();
        user.getSaveList().add(saveGoal);
        saveRepository.save(saveGoal);
        return responseService.getSingleResponse(HttpStatus.OK.value(), new SaveResponse(saveGoal));

    }

    //절약 목표 수정
    public CommonResponse updateSaveGoal(CustomUserPrincipal customUserPrincipal, Long savedId,SaveRequest saveRequest) {
        Users user=userRepository.findById(customUserPrincipal.getId())
                .orElseThrow(()->new NotFoundException("could not found user!"));
        // To do : save 명칭 변경
        Save save = saveRepository.findById(savedId).orElseThrow(() -> new NotFoundException("could not found save goal"));

        save.updateSave(saveRequest);
        saveRepository.save(save);
        return responseService.getSingleResponse(HttpStatus.OK.value(), new SaveResponse(save));

    }
    //절약 현황 (현재까지의 지출 금액 / 목표 금액)


}
