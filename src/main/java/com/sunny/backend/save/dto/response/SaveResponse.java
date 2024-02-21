package com.sunny.backend.save.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sunny.backend.save.domain.Save;
import java.time.LocalDate;

public record SaveResponse(
    Long id,
    Long cost,
    boolean expire,
    boolean success,
    @JsonFormat(pattern = "yyyy.MM.dd")
    LocalDate startDate,
    @JsonFormat(pattern = "yyyy.MM.dd")
    LocalDate endDate
) {
    public static SaveResponse from(Save save,boolean success) {
        return new SaveResponse(
            save.getId(),
            save.getCost(),
            save.checkExpired(save.getEndDate()),
            success,
            save.getStartDate(),
            save.getEndDate()
        );
    }
    public record DetailSaveResponse(
        long date,
        double savePercentage,
        Long cost

    ) {
        public static DetailSaveResponse of(long date, double savePercentage,long cost) {
            return new DetailSaveResponse(
                date,
                savePercentage,
                cost
            );
        }
    }
}