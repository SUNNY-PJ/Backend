package com.sunny.backend.dto.response.save;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sunny.backend.entity.Save;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
public class SaveResponse {

    private Long id;
    private Long cost;
    private String saveContent;
    private String startDate;

    private String endDate;


    public SaveResponse(Save save) {
        this.id = save.getId();
        this.cost = save.getCost();
        this.saveContent = save.getSaveContent();
        this.startDate = save.getStartDate();
        this.endDate = save.getEndDate();

    }
}
