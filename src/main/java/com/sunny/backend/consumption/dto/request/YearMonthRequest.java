package com.sunny.backend.consumption.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.YearMonth;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

@ToString
@Getter
@NoArgsConstructor
public class YearMonthRequest {
  @NotNull(message = "해당 년/월을 입력해주세요. (ex)'2024.01'")
  @DateTimeFormat(pattern = "yyyy.MM")
  @JsonFormat(shape = Shape.STRING,pattern = "yyyy.MM",timezone ="Asia/Seoul" )
  private YearMonth yearMonth;
}
