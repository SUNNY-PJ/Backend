package com.sunny.backend.dto.response.save;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sunny.backend.save.domain.Save;
import java.time.LocalDate;

public record SaveResponse(

    Long id,
    Long cost,
    @JsonFormat(pattern = "yyyy.MM.dd")
    LocalDate startDate,
    @JsonFormat(pattern = "yyyy.MM.dd")
    LocalDate endDate
) {

    public static SaveResponse from(Save save) {
        return new SaveResponse(
            save.getId(),
            save.getCost(),
            save.getStartDate(),
            save.getEndDate()
        );
    }

    public record DetailSaveResponse(

        long date,
        double savePercentage

    ) {

        public static DetailSaveResponse of(long date, double savePercentage) {
            return new DetailSaveResponse(
                date,
                savePercentage
            );
        }
    }
}