package com.sunny.backend.entity;

import com.sunny.backend.dto.request.save.SaveRequest;
import com.sunny.backend.user.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

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

    @Column
    private Long cost;

    @Column
    private String saveContent;

    @Column
    private String startDate;

    @Column
    private String endDate;
    @OneToOne
    @JoinColumn(name = "user_id")
    private Users users;

    public void updateSave(SaveRequest saveRequest){
        this.cost=saveRequest.getCost();
        this.saveContent=saveRequest.getSaveContent();
        this.startDate=saveRequest.getStartDate();
        this.endDate=saveRequest.getEndDate();
    }
}
