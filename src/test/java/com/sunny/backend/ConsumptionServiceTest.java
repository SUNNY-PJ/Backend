package com.sunny.backend;


import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.CommonResponse.SingleResponse;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.dto.request.consumption.ConsumptionRequest;
import com.sunny.backend.dto.response.consumption.ConsumptionResponse;
import com.sunny.backend.dto.response.consumption.SpendTypeStatisticsResponse;
import com.sunny.backend.entity.Consumption;
import com.sunny.backend.entity.SpendType;
import com.sunny.backend.repository.consumption.ConsumptionRepository;
import com.sunny.backend.service.consumption.ConsumptionService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.sunny.backend.security.service.CustomUserDetailsService;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.user.Role;
import com.sunny.backend.user.Users;
import com.sunny.backend.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@SpringBootTest
class ConsumptionTest {

  @Autowired
  WebApplicationContext context;

  @Autowired
  UserRepository userRepository;

  @Autowired
  ConsumptionRepository consumptionRepository;

  @Autowired //service 주입
  ConsumptionService consumptionService;

  @Autowired
  CustomUserDetailsService customUserDetailsService;

  CustomUserPrincipal customUserPrincipal;

  MockMvc mockMvc;
  Users users;
  Consumption consumption;

  @BeforeEach
    //각각의 테스트 시작 전
  void setUp() {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(springSecurity())
        .build();
    setTestUsersAndIssueToken();
  }

  private void setTestUsersAndIssueToken() {
    String userName = "유저이름";
    String userEmail = "user@naver.com";
    String friendName = "친구이름";
    String friendEmail = "UserFriend@naver.com";

    users = createUser(userName, userEmail);

    // testUser의 토큰 반환
    customUserPrincipal = customUserDetailsService.loadUserByUsername(userEmail);
  }

  private Users createUser(String name, String email) {
    Users saveUser = Users.builder()
        .name(name)
        .email(email)
        .role(Role.USER)
        .build();
    return userRepository.save(saveUser);
  }

  @Nested
  class 지출_테스트 {

    @Test
    @WithMockUser
    void 지출_등록하기() throws Exception {
      // given
      ConsumptionRequest consumptionRequest = new ConsumptionRequest();
      consumptionRequest.setName("집");
      consumptionRequest.setCategory(SpendType.SHELTER);
      consumptionRequest.setMoney(10000000L);

      consumptionRequest.setDateField(LocalDate.now());

      // when
      mockMvc.perform(post("/consumption")
              .content(asJsonString(consumptionRequest))
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk());

      // then
      ResponseEntity<SingleResponse<ConsumptionResponse>> actualResponseEntity =
          consumptionService.createConsumption(customUserPrincipal, consumptionRequest);

      assertThat(actualResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

      SingleResponse<ConsumptionResponse> responseBody = actualResponseEntity.getBody();
      assertThat(responseBody).isNotNull();

      ConsumptionResponse actual = responseBody.getData();
      assertThat(actual)
          .extracting("name", "category", "money", "dateField")
          .containsExactly("집", SpendType.SHELTER, 10000000L, LocalDate.now());
    }

//    @Test
//    void updateConsumptionTest() throws Exception {
//      // Similar to the above test, provide the correct values for assertions
//      ConsumptionRequest consumptionRequest = new ConsumptionRequest();
//      consumptionRequest.setName("집");
//      consumptionRequest.setCategory(SpendType.주거);
//      consumptionRequest.setMoney(100000000L);
//
//      consumptionRequest.setDateField(LocalDate.now());
//
//      // when
//      mockMvc.perform(put("/consumption/1")
//              .content(asJsonString(consumptionRequest))
//              .contentType(MediaType.APPLICATION_JSON))
//          .andExpect(status().isOk());
//
//      // then
//      ResponseEntity<SingleResponse<ConsumptionResponse>> actualResponseEntity =
//          consumptionService.createConsumption(customUserPrincipal, consumptionRequest);
//
//      assertThat(actualResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//      SingleResponse<ConsumptionResponse> responseBody = actualResponseEntity.getBody();
//      assertThat(responseBody).isNotNull();
//
//      ConsumptionResponse actual = responseBody.getData();
//      assertThat(actual)
//          .extracting("name", "category", "money", "dateField")
//          .containsExactly("집", SpendType.주거, 100000000L, LocalDate.now());
//    }

//    @Test
//    void getSpendTypeStatisticsTest() {
//      // Assuming SpendType and other required values
//      SpendType spendType = SpendType.식생활;
//      long value1 = 10L;
//      long value2 = 20L;
//      double value3 = 30.5;
//
//      List<SpendTypeStatisticsResponse> statistics = Arrays.asList(
//          new SpendTypeStatisticsResponse(spendType, value1, value2, value3));
//      when(consumptionRepository.getSpendTypeStatistics()).thenReturn(statistics);
//
//
//      ResponseEntity<CommonResponse.ListResponse<SpendTypeStatisticsResponse>> response =
//          consumptionService.getSpendTypeStatistics();
//
//      assertEquals(200, response.getStatusCodeValue());
//      // Add more assertions as needed
//    }

    private static String asJsonString(Object object) {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

      try {
        return objectMapper.writeValueAsString(object);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }


    private Consumption createConsumption(Users users, ConsumptionRequest consumptionRequest) {
      Consumption saveConsumption = Consumption.builder()
          .name(consumptionRequest.getName())
          .category(consumptionRequest.getCategory())
          .money(consumptionRequest.getMoney())
          .dateField(consumptionRequest.getDateField())
          .users(users)
          .build();
      return consumptionRepository.save(saveConsumption);
    }
  }
}