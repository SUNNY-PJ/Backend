package com.sunny.backend.consumption.service;



import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.dto.request.consumption.ConsumptionRequest;
import com.sunny.backend.dto.response.consumption.ConsumptionResponse;
import com.sunny.backend.dto.response.consumption.SpendTypeStatisticsResponse;
import com.sunny.backend.consumption.domain.Consumption;
import com.sunny.backend.consumption.domain.SpendType;
import com.sunny.backend.consumption.repository.ConsumptionRepository;
import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.user.domain.Users;
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

    @Transactional
    public ResponseEntity<CommonResponse.SingleResponse<ConsumptionResponse>> createConsumption(
            CustomUserPrincipal customUserPrincipal, ConsumptionRequest consumptionRequest) {
        Users user = customUserPrincipal.getUsers();
        Consumption consumption = Consumption.builder()
            .name(consumptionRequest.getName())
            .category(consumptionRequest.getCategory())
            .money(consumptionRequest.getMoney())
            .dateField(consumptionRequest.getDateField())
            .users(user)
            .build();

        consumptionRepository.save(consumption);
        if (user.getConsumptionList() == null) {
            user.addConsumption(consumption);
        }
        ConsumptionResponse consumptionResponse = ConsumptionResponse.from(consumption);
        return responseService.getSingleResponse(HttpStatus.OK.value(),
            consumptionResponse, "지출을 등록했습니다.");
    }

    @Transactional
    public ResponseEntity<CommonResponse.ListResponse<ConsumptionResponse>> getConsumptionList(
            CustomUserPrincipal customUserPrincipal) {
        List<Consumption> consumptions = consumptionRepository.findByUsersId(
            customUserPrincipal.getUsers().getId());
        List<ConsumptionResponse> consumptionResponses = ConsumptionResponse.listFrom(consumptions);
        return responseService.getListResponse(HttpStatus.OK.value(),
            consumptionResponses, "지출 내역을 불러왔습니다.");
    }

    @Transactional
    public ResponseEntity<CommonResponse.SingleResponse<ConsumptionResponse>> updateConsumption(
        CustomUserPrincipal customUserPrincipal,
        ConsumptionRequest consumptionRequest, Long consumptionId) {
        Users user = customUserPrincipal.getUsers();
        Consumption consumption = consumptionRepository.getById(consumptionId);
        Consumption.validateConsumptionByUser(user.getId(), consumption.getUsers().getId());
        consumption.updateConsumption(consumptionRequest);
        ConsumptionResponse consumptionResponse = ConsumptionResponse.from(consumption);
        return responseService.getSingleResponse(HttpStatus.OK.value(),
            consumptionResponse, "지출을 수정했습니다.");
    }

    @Transactional
    public ResponseEntity<CommonResponse.ListResponse<SpendTypeStatisticsResponse>> getSpendTypeStatistics(
        CustomUserPrincipal customUserPrincipal) {
        Users user = customUserPrincipal.getUsers();
        List<SpendTypeStatisticsResponse> statistics = consumptionRepository.getSpendTypeStatistics(
            user.getId());
        return responseService.getListResponse(HttpStatus.OK.value(),
            statistics, "지출 통계 내역을 불러왔습니다.");
    }

    @Transactional
    public ResponseEntity<CommonResponse.ListResponse<ConsumptionResponse.DetailConsumptionResponse>>
    getDetailConsumption(CustomUserPrincipal customUserPrincipal, LocalDate datefield) {
        List<Consumption> detailConsumption =
            consumptionRepository.findByUsersIdAndDateField(customUserPrincipal.getUsers().getId(),
                datefield);
        List<ConsumptionResponse.DetailConsumptionResponse> detailConsumptions =
            ConsumptionResponse.DetailConsumptionResponse.listFrom(detailConsumption);
        return responseService.getListResponse(HttpStatus.OK.value(),
            detailConsumptions, datefield + "에 맞는 지출 내역을 불러왔습니다.");
    }

    @Transactional
    public ResponseEntity<CommonResponse.GeneralResponse> deleteConsumption(
        CustomUserPrincipal customUserPrincipal, Long consumptionId) {

        Users user = customUserPrincipal.getUsers();
        Consumption consumption = consumptionRepository.getById(consumptionId);
        Consumption.validateConsumptionByUser(user.getId(), consumption.getUsers().getId());
        consumptionRepository.deleteById(consumptionId);
        return responseService.getGeneralResponse(HttpStatus.OK.value(),
            "지출 내역을 삭제했습니다.");
    }

    @Transactional
    public ResponseEntity<CommonResponse.ListResponse<ConsumptionResponse.DetailConsumptionResponse>>
    getConsumptionByCategory(CustomUserPrincipal customUserPrincipal, SpendType spendType) {
        List<Consumption> detailConsumption =
            consumptionRepository.findByUsersIdAndCategory(customUserPrincipal.getUsers().getId(),
                spendType);
        List<ConsumptionResponse.DetailConsumptionResponse> detailConsumptions =
            ConsumptionResponse.DetailConsumptionResponse.listFrom(detailConsumption);
        return responseService.getListResponse(HttpStatus.OK.value(),
            detailConsumptions, spendType + "에 맞는 지출 내역을 불러왔습니다.");
    }
}
