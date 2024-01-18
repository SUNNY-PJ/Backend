package com.sunny.backend.dto.response.consumption;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.sunny.backend.consumption.domain.Consumption;
import com.sunny.backend.consumption.domain.SpendType;
import java.time.LocalDate;
import java.util.List;

public record ConsumptionResponse(

    Long id,
    String name,
    SpendType category,
    Long money,
    @JsonFormat(pattern = "yyyy.MM.dd")
    LocalDate dateField
) {

    public static ConsumptionResponse from(Consumption consumption) {
        return new ConsumptionResponse(
            consumption.getId(),
            consumption.getName(),
            consumption.getCategory(),
            consumption.getMoney(),
            consumption.getDateField()
        );
    }

    public static List<ConsumptionResponse> listFrom(List<Consumption> consumptions) {
        return consumptions.stream()
            .map(ConsumptionResponse::from)
            .toList();
    }

    public record DetailConsumptionResponse(
        Long id,
        String name,
        Long money
    ) {

        public static DetailConsumptionResponse from(Consumption consumption) {
            return new DetailConsumptionResponse(
                consumption.getId(),
                consumption.getName(),
                consumption.getMoney()
            );
        }

        public static List<DetailConsumptionResponse> listFrom(List<Consumption> consumptions) {
            return consumptions.stream()
                .map(DetailConsumptionResponse::from)
                .toList();
        }
    }
}