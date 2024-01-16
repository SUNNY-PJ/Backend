package com.sunny.backend.competition.domain;

import com.sunny.backend.common.CommonCustomException;
import com.sunny.backend.common.CommonErrorCode;
import com.sunny.backend.common.CustomException;
import com.sunny.backend.competition.exception.CompetitionErrorCode;
import com.sunny.backend.friends.domain.Status;
import com.sunny.backend.friends.exception.FriendErrorCode;
import com.sunny.backend.user.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String output; // 결과

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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;

    @ManyToOne
    @JoinColumn(name = "user_friend_id")
    private Users userFriend;

    public void approveStatus() {
        status = Status.APPROVE;
    }

    public void validateStatus() {
        if(status.equals(Status.WAIT)) {
            throw new CustomException(CompetitionErrorCode.COMPETITION_NOT_APPROVE);
        }
        if(status.equals(Status.APPROVE)) {
            throw new CustomException(CompetitionErrorCode.COMPETITION_EXIST);
        }
    }

    public void validateCompetitionByUser(Long userId, Long tokenUserId) {
        if(!userId.equals(tokenUserId)) {
            throw new CommonCustomException(CommonErrorCode.TOKEN_INVALID);
        }
    }
}
