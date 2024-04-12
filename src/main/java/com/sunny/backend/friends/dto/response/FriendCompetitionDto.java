package com.sunny.backend.friends.dto.response;

import java.time.LocalDate;

import com.sunny.backend.competition.domain.CompetitionOutputStatus;
import com.sunny.backend.competition.domain.CompetitionStatus;
import com.sunny.backend.friends.domain.FriendCompetitionStatus;
import com.sunny.backend.friends.domain.FriendStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FriendCompetitionDto {
	private Long friendId;
	private Long userFriendId;
	private String nickname;
	private String profile;
	private FriendStatus friendStatus;
	private Long competitionId;
	private String message;
	private LocalDate startDate;
	private LocalDate endDate;
	private Long price;
	private String compensation;
	private FriendCompetitionStatus friendCompetitionStatus;
	private CompetitionOutputStatus competitionOutputStatus;
	private CompetitionStatus competitionStatus;
}
