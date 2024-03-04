package com.sunny.backend.save.dto.response;


import com.sunny.backend.save.domain.Save;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record SaveResponse(
    Long id,
    Long cost,
    boolean expire,
    boolean success,
    String startDate,
    String endDate
) {
    public static SaveResponse from(Save save, boolean success) {
        return new SaveResponse(
            save.getId(),
            save.getCost(),
            save.checkExpired(save.getEndDate()),
            success,
            formatStartDateWithDayOfWeek(save.getStartDate()),
            formatStartDateWithDayOfWeek(save.getEndDate())
        );
    }

    private static String formatStartDateWithDayOfWeek(LocalDate date) {
        // 원하는 형태의 포맷으로 날짜와 요일을 함께 표현
        return date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd EEEE"));
    }

    public record DetailSaveResponse(
        long date,
        double savePercentage,
        Long cost
    ) {
        public static DetailSaveResponse of(long date, double savePercentage, long cost) {
            return new DetailSaveResponse(
                date,
                savePercentage,
                cost
            );
        }
    }
}