package com.sunny.backend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;

public class AppleFeignClientConfig {
  @Bean
  public AppleFeignClientErrorDecoder appleFeignClientErrorDecoder() {
    return new AppleFeignClientErrorDecoder(new ObjectMapper());
  }

}
