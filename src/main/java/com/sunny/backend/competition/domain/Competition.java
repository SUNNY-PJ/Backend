package com.sunny.backend.competition.domain;

import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.competition.exception.CompetitionErrorCode;
import com.sunny.backend.friends.domain.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //경쟁 id

    @Column
    private String message; // 도발 메세지

    @Column
    @Enumerated(value = EnumType.STRING)
    private CompetitionStatus output; // 결과

    @Column
    private LocalDate startDate; // 시작 기간

    @Column
    private LocalDate endDate; // 종료 기간

    @Column
    private Long price; // 대결 금액

    @Column
    private String compensation; // 대결 보상

    @Column
    @Enumerated(value = EnumType.STRING)
    private Status status;

    public void approveStatus() {
        status = Status.APPROVE;
    }
    public void updateOutput(CompetitionStatus competitionStatus){
        this.output=competitionStatus;

    }

    public void validateStatus() {
        if(status.equals(Status.WAIT)) {
            throw new CustomException(CompetitionErrorCode.COMPETITION_NOT_APPROVE);
        }
        if(status.equals(Status.APPROVE)) {
            throw new CustomException(CompetitionErrorCode.COMPETITION_EXIST);
        }
    }

}
