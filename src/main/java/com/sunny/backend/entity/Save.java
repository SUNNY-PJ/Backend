package com.sunny.backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sunny.backend.dto.request.community.CommunityRequest;
import com.sunny.backend.dto.request.save.SaveRequest;
import com.sunny.backend.user.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Save {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="save_id")
    private Long id;

    //절약 목표 금액
    @Column
    private Long cost;

    //절약 시작 , 시간까지 포함 -> datetime 수정
    @Column
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    //절약 종료 , 시간까지 포함 -> datetime 수정
    @Column
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    //절약 종료 기간

    //유저
    // To do : (1:1 관계) 수정
    @OneToOne
    @JoinColumn(name = "user_id")
    private Users users;

    public void updateSave(SaveRequest saveRequest){
        this.cost=saveRequest.getCost();
        this.startDate=saveRequest.getStartDate();
        this.endDate=saveRequest.getEndDate();

    }
}
