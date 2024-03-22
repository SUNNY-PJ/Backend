package com.sunny.backend.auth.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserRequest {
  @NotEmpty(message = "잘못된 요청입니다.")
  private String accessToken;

  @NotEmpty(message = "잘못된 요청입니다.")
  private String refreshToken;
}
