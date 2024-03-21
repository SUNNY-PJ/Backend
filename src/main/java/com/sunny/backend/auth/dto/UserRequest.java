package com.sunny.backend.auth.dto;

import lombok.Getter;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
public class UserRequest {
  @NotEmpty(message = "잘못된 요청입니다.")
  private String accessToken;

  @NotEmpty(message = "잘못된 요청입니다.")
  private String refreshToken;
}
