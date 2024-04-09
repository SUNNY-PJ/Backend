package com.sunny.backend.consumption.dto.request;

import java.time.YearMonth;

import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
public class YearMonthRequest {
	@NotNull(message = "해당 년/월을 입력해주세요. (ex)'2024.01'")
	@DateTimeFormat(pattern = "yyyy.MM")
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy.MM", timezone = "Asia/Seoul")
	private YearMonth yearMonth;
}
