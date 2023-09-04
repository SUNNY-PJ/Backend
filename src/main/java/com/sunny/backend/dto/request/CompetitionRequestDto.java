package com.sunny.backend.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

import java.time.LocalDate;

public class CompetitionRequestDto {

    @Getter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class CompetitionApply {
        private Long friendsId;
        private String message;
        private Integer price;
        private String compensation;
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Getter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class CompetitionAccept {
        private Long competitionId;
        private Character approve;
    }

}
