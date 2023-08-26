package com.sunny.backend.dto.request.consumption;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ConsumptionRequest {

    private String name; //지출명
    private String place; //지출 장소
    private Long money; //지출 금액

    private String  dateField; // 지출 일자

    public Date getParsedDateField() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(dateField.split(" ")[0]);
        } catch (ParseException e) {
            // Handle date parsing exception, or return null/throw exception based on your requirement
            return null;
        }
    }

    public void setDateField(String dateField) {
        this.dateField = dateField;
    }
}





