package com.sunny.backend.dto.response.save;

import com.sunny.backend.entity.Save;
import java.time.LocalDate;

public record SaveResponse(

    Long id,
    Long cost,
    String saveContent,
    LocalDate startDate,
    LocalDate endDate
) {

    public static SaveResponse from(Save save) {
        return new SaveResponse(
            save.getId(),
            save.getCost(),
            save.getSaveContent(),
            save.getStartDate(),
            save.getEndDate()
        );
    }
}
