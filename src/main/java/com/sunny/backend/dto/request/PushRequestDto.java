package com.sunny.backend.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PushRequestDto {
    @NotNull(message = "전송하려는 사용자의 ID는 필수 값입니다.")
    private Long friendsId;
    @NotBlank(message = "알림 제목은 필수 값입니다.")
    private String title;
    @NotBlank(message = "알림 내용은 필수 값입니다.")
    private String body;
}
