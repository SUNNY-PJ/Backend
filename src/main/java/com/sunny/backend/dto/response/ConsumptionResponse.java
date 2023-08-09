package com.sunny.backend.dto.response;


import com.sunny.backend.entity.*;
import lombok.Getter;
import org.joda.time.DateTime;


import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ConsumptionResponse {

    private String name; //지출명
    private SpendType category; // 카테고리

    private Long money; //지출 금액

    private Date dateField; //지출 일자

    public ConsumptionResponse(Consumption consumption) {

        this.name = consumption.getName();
        this.category = consumption.getCategory();
        this.money=consumption.getMoney();
        this.dateField=consumption.getDateField();

    }

    public static List<ConsumptionResponse> fromConsumptions(List<Consumption> consumptions) {
        return consumptions.stream()
                .map(ConsumptionResponse::new)
                .collect(Collectors.toList());
    }

}
