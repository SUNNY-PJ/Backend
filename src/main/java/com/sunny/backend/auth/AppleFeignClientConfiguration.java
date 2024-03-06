package com.sunny.backend.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.text.SimpleDateFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//public class AppleFeignClientConfiguration {
//  @Bean
//  public AppleFeignClientErrorDecoder appleFeignClientErrorDecoder() {
//    return new AppleFeignClientErrorDecoder(new ObjectMapper());
//  }
//}


//@Configuration
//public class AppleFeignClientConfiguration {
//
//  @Bean
//  public ObjectMapper objectMapper() {
//    ObjectMapper objectMapper = new ObjectMapper();
//    // 원하는 ObjectMapper 설정을 추가할 수 있습니다.
//    return objectMapper;
//  }
//}

@Configuration
public class AppleFeignClientConfiguration {

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();

    // 날짜 직렬화를 위한 설정
    JavaTimeModule javaTimeModule = new JavaTimeModule();
    objectMapper.registerModule(javaTimeModule);

    // LocalDate를 원하는 형식으로 직렬화하기 위한 설정 추가
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // NULL 값은 직렬화하지 않음
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 날짜를 timestamp 형태로 출력하지 않음
    objectMapper.setDateFormat(new SimpleDateFormat("yyyy.MM.dd")); // 원하는 날짜 형식으로 지정

    return objectMapper;
  }
}