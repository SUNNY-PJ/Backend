package com.sunny.backend.save.domain;


import com.sunny.backend.save.dto.request.SaveRequest;
import com.sunny.backend.user.domain.Users;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.PositiveOrZero;
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
    @Column(name = "save_id")
    private Long id;

    @Column
    @PositiveOrZero
    private Long cost;

    @Column
    @FutureOrPresent
    private LocalDate startDate;
    @Column
    @FutureOrPresent
    private LocalDate endDate;
    @OneToOne
    @JoinColumn(name = "user_id")
    private Users users;

    public void updateSave(SaveRequest saveRequest) {
        this.cost = saveRequest.getCost();
        this.startDate = saveRequest.getStartDate();
        this.endDate = saveRequest.getEndDate();
    }

    public long calculateRemainingDays(Save save) {
        LocalDate currentDate = LocalDate.now();
        return ChronoUnit.DAYS.between(currentDate, save.getEndDate());
    }

    public double calculateSavePercentage(Long userMoney, Save save) {
        double percentage = userMoney != null ?
            100.0 - (((double) userMoney / (double) save.getCost()) * 100.0) : 100.0;
        BigDecimal roundedPercentage = new BigDecimal(percentage).setScale(1, RoundingMode.HALF_UP);
        return roundedPercentage.doubleValue();
    }
}