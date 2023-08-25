package com.sunny.backend.service.consumption;

import com.sunny.backend.dto.request.ConsumptionRequest;
import com.sunny.backend.dto.response.ConsumptionResponse;
import com.sunny.backend.dto.response.Response;
import com.sunny.backend.dto.response.SpendTypeStatisticsResponse;
import com.sunny.backend.entity.Consumption;
import com.sunny.backend.entity.SpendType;

import com.sunny.backend.repository.consumption.ConsumptionRepository;
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
    //지출 등록
    public ResponseEntity createConsumption(Users users, ConsumptionRequest consumptionRequest) throws IOException {
        try {
            Consumption consumption = Consumption.builder()
                    .name(consumptionRequest.getName())
                    .category(SpendType.valueOf(consumptionRequest.getPlace()))
                    .money(consumptionRequest.getMoney())
                    .dateField(consumptionRequest.getParsedDateField())
                    .users(users)
                    .build();

            Consumption saveConsumption=consumptionRepository.save(consumption);
            Users saveUsers = usersRepository.findById(users.getId()).get(); // 이 작업이 필요한 작업인지?
            saveUsers.getConsumptionList().add(consumption);

            return response.success(new ConsumptionResponse(consumption), "지출 등록 성공", HttpStatus.OK);
        } catch (Exception e) {
            return response.fail(e,"지출 등록 실패",HttpStatus.BAD_REQUEST);
        }
    }


    public ResponseEntity<?> getConsumptionList(Users user) throws IOException {
        try {
            List<Consumption> consumptions = consumptionRepository.findByUsersId(user.getId());
            List<ConsumptionResponse> consumptionResponses = ConsumptionResponse.fromConsumptions(consumptions);

            return response.success(consumptionResponses, "지출 조회 성공", HttpStatus.OK);
        } catch (Exception e) {
            return response.fail(e, "지출 조회 실패", HttpStatus.BAD_REQUEST);
        }
    }

    public List<SpendTypeStatisticsResponse> getSpendTypeStatistics() {
        return consumptionRepository.getSpendTypeStatistics();
    }

}
