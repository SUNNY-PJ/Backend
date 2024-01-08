package com.sunny.backend.dto.response.consumption;


import com.sunny.backend.entity.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ConsumptionResponse {

    private Long id;
    private String name;
    private SpendType category;
    private Long money;
    private LocalDate dateField;

    public ConsumptionResponse(Consumption consumption) {
        this.id = consumption.getId();
        this.name = consumption.getName();
        this.category = consumption.getCategory();
        this.money = consumption.getMoney();
        this.dateField = consumption.getDateField();
    }

    public static List<ConsumptionResponse> fromConsumptions(List<Consumption> consumptions) {
        return consumptions.stream()
                .map(ConsumptionResponse::new)
                .collect(Collectors.toList());
    }

    @Getter
    public static class DetailConsumption {

        private String name;
        private Long money;

        public DetailConsumption(Consumption consumption) {
            this.name = consumption.getName();
            this.money = consumption.getMoney();
        }

        public static List<ConsumptionResponse.DetailConsumption> fromDetailConsumptions(
            List<Consumption> consumptions) {
            return consumptions.stream()
                .map(ConsumptionResponse.DetailConsumption::new)
                .collect(Collectors.toList());
        }
    }
}

