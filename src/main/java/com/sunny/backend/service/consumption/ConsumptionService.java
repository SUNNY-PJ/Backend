package com.sunny.backend.service.consumption;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.dto.request.consumption.ConsumptionRequest;
import com.sunny.backend.dto.response.consumption.ConsumptionResponse;
import com.sunny.backend.dto.response.consumption.SpendTypeStatisticsResponse;
import com.sunny.backend.entity.Consumption;
import com.sunny.backend.repository.consumption.ConsumptionRepository;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.user.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumptionService {

    private final ConsumptionRepository consumptionRepository;
    private final ResponseService responseService;

    //지출 등록
    @Transactional
    public ResponseEntity<CommonResponse.SingleResponse<ConsumptionResponse>> createConsumption(
            CustomUserPrincipal customUserPrincipal, ConsumptionRequest consumptionRequest) {
        Users user = customUserPrincipal.getUsers();
        Consumption consumption = Consumption.builder()
                .name(user.getName())
                .category(consumptionRequest.getCategory())
                .money(consumptionRequest.getMoney())
                .dateField(consumptionRequest.getDateField())
                .users(user)
                .build();

        consumptionRepository.save(consumption);

        if (user.getConsumptionList() == null) {
            user.addConsumption(consumption);
        }
        return responseService.getSingleResponse(HttpStatus.OK.value(), new ConsumptionResponse(consumption),
                "지출을 등록했습니다.");
    }

    @Transactional
    //지출 조회
    public ResponseEntity<CommonResponse.ListResponse<ConsumptionResponse>> getConsumptionList(
            CustomUserPrincipal customUserPrincipal) {
        //유저 id 기준으로 지출 내역 조회
        List<Consumption> consumptions = consumptionRepository.findByUsersId(customUserPrincipal.getUsers().getId());
        List<ConsumptionResponse> consumptionResponses = ConsumptionResponse.fromConsumptions(consumptions);
        return responseService.getListResponse(HttpStatus.OK.value(), consumptionResponses, "지출 내역을 불러왔습니다.");
    }

    @Transactional
    //지출 통계
    public ResponseEntity<CommonResponse.ListResponse<SpendTypeStatisticsResponse>> getSpendTypeStatistics() {
        List<SpendTypeStatisticsResponse> statistics = consumptionRepository.getSpendTypeStatistics();
        return responseService.getListResponse(HttpStatus.OK.value(), statistics, "지출 통계 내역을 불러왔습니다.");
    }

    @Transactional
    //지출 내역 조회
    public ResponseEntity<CommonResponse.ListResponse<ConsumptionResponse.DetailConsumption>> getDetailConsumption(
            CustomUserPrincipal customUserPrincipal, LocalDate datefield) {
        List<Consumption> detailConsumption =
                consumptionRepository.findByUsersIdAndDateField(customUserPrincipal.getUsers().getId(), datefield);
        List<ConsumptionResponse.DetailConsumption> detailConsumptions =
                ConsumptionResponse.DetailConsumption.fromDetailConsumptions(detailConsumption);
        return responseService.getListResponse(HttpStatus.OK.value(),
                detailConsumptions, datefield + " 에 맞는 지출 내역을 불러왔습니다.");
    }

}
