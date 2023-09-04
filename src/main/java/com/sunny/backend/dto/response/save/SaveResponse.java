package com.sunny.backend.dto.response.save;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sunny.backend.entity.Save;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
public class SaveResponse {
    private Long cost;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;



    public SaveResponse(Save save){
        this.cost=save.getCost();
        this.startDate=save.getStartDate();
        this.endDate=save.getEndDate();

    }
}
