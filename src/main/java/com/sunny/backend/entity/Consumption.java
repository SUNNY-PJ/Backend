package com.sunny.backend.entity;

import static com.sunny.backend.common.CommonErrorCode.*;

import com.sunny.backend.common.CommonCustomException;
import com.sunny.backend.common.CustomException;
import com.sunny.backend.dto.request.consumption.ConsumptionRequest;
import com.sunny.backend.entity.friends.ApproveType;
import com.sunny.backend.user.Users;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class Consumption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Enumerated(value = EnumType.STRING)
    private SpendType category;

    @Column
    @NotBlank(message = "지출 이름은 필수 입력값입니다.")
    private String name;

    @Column
    @NotNull(message = "지출 금액은 필수 입력값입니다.")
    private Long money;

    @Column
    @NotNull(message = "지출 날짜는 필수 입력값입니다.")
    private LocalDate dateField;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users users;

    public void updateConsumption(ConsumptionRequest consumptionRequest){
        this.category=consumptionRequest.getCategory();
        this.name=consumptionRequest.getName();
        this.money=consumptionRequest.getMoney();
        this.dateField=consumptionRequest.getDateField();

    }
    public static void validateConsumptionByUser(Long userId, Long consumptionUserId) {
        if(!userId.equals(consumptionUserId)) {
            throw new CommonCustomException(NO_USER_PERMISSION);
        }
    }
}