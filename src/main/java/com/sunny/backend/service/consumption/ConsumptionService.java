

package com.sunny.backend.service.consumption;

import com.amazonaws.services.kms.model.NotFoundException;
import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.dto.request.consumption.ConsumptionRequest;
import com.sunny.backend.dto.response.community.CommunityResponse;
import com.sunny.backend.dto.response.consumption.ConsumptionResponse;
import com.sunny.backend.dto.response.Response;
import com.sunny.backend.dto.response.consumption.SpendTypeStatisticsResponse;
import com.sunny.backend.entity.Consumption;
import com.sunny.backend.entity.SpendType;

import com.sunny.backend.repository.consumption.ConsumptionRepository;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.user.Users;
import com.sunny.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumptionService {
    private final ConsumptionRepository consumptionRepository;
    private final UserRepository usersRepository;
    private final Response response;
    private final ResponseService responseService;
    //지출 등록
    public CommonResponse createConsumption(CustomUserPrincipal customUserPrincipal, ConsumptionRequest consumptionRequest) {
        Users user=usersRepository.findById(customUserPrincipal.getId())
                .orElseThrow(()->new NotFoundException("could not found user"));
        Consumption consumption = Consumption.builder()
                .name(user.getName())
                .category(SpendType.valueOf(consumptionRequest.getPlace()))
                .money(consumptionRequest.getMoney())
                .dateField(consumptionRequest.getParsedDateField())
                .users(user)
                .build();

        Consumption saveConsumption=consumptionRepository.save(consumption);
        user.getConsumptionList().add(consumption);

        return responseService.getSingleResponse(HttpStatus.OK.value(),new ConsumptionResponse(consumption));
    }


    //지출 조회
    public CommonResponse getConsumptionList(CustomUserPrincipal customUserPrincipal) {
        //유저 id 기준으로 지출 내역 조회
        List<Consumption> consumptions = consumptionRepository.findByUsersId(customUserPrincipal.getId());
        List<ConsumptionResponse> consumptionResponses = ConsumptionResponse.fromConsumptions(consumptions);

        return responseService.getListResponse(HttpStatus.OK.value(), consumptionResponses);

    }

    //지출 통계
    public CommonResponse.ListResponse getSpendTypeStatistics() {
        List<SpendTypeStatisticsResponse> statistics = consumptionRepository.getSpendTypeStatistics();
        return responseService.getListResponse(HttpStatus.OK.value(), statistics);
    }

}

