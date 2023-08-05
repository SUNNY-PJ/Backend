package com.sunny.backend.service;

import com.sunny.backend.dto.request.ConsumptionRequest;
import com.sunny.backend.dto.response.ConsumptionResponse;
import com.sunny.backend.dto.response.Response;
import com.sunny.backend.entity.Consumption;
import com.sunny.backend.repository.consumption.ConsumptionRepository;
import com.sunny.backend.user.Users;
import com.sunny.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

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
                    .place(consumptionRequest.getPlace())
                    .name(consumptionRequest.getName())  //유저 값 생기면 수정 .users.getName()
                    .money(consumptionRequest.getMoney())
                    .dateField(consumptionRequest.getDateField())
                    .users(users)
                    .build();

            Consumption saveConsumption=consumptionRepository.save(consumption);
            Users saveUsers = usersRepository.findById(users.getId()).get(); // 이 작업이 필요한 작업인지?
            saveUsers.getConsumptionList().add(consumption);

            return response.success(new ConsumptionResponse(consumption), "지출 등록 성공", HttpStatus.OK);
        } catch (Exception e) {
            return response.fail(e,"컨테스트 글 등록 실패",HttpStatus.BAD_REQUEST);
        }
    }
}
