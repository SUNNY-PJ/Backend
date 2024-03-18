package com.sunny.backend.save.dto.response;

import com.sunny.backend.save.domain.Save;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
public record SaveResponse(
    Long id,
    Long cost,
    boolean expire,
    boolean success,
    LocalDate startDate,
    LocalDate endDate
) {
    public static SaveResponse from(Save save, boolean success) {
        return new SaveResponse(
            save.getId(),
            save.getCost(),
            save.checkExpired(save.getEndDate()),
            success,
            save.getStartDate(), // 수정
           save.getEndDate()    // 수정
        );
    }

    private static String formatStartDateWithDayOfWeek(LocalDate date) {
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd EEEE")); // 수정
        return formattedDate;
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
    public record SaveListResponse (
        Long id,
        Long cost,
        boolean expire,
        boolean success,
        String startDate,
        String endDate
    ) {
        public static SaveListResponse from(Save save, boolean success) {
            return new SaveListResponse(
                save.getId(),
                save.getCost(),
                save.checkExpired(save.getEndDate()),
                success,
                formatStartDateWithDayOfWeek(save.getStartDate()),
                formatStartDateWithDayOfWeek(save.getEndDate())
            );
        }
    }
}