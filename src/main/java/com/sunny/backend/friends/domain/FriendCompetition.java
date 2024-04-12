package com.sunny.backend.friends.domain;

import static com.sunny.backend.competition.exception.CompetitionErrorCode.*;
import static lombok.AccessLevel.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.sunny.backend.common.BaseTime;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.competition.domain.Competition;
import com.sunny.backend.competition.domain.CompetitionOutputStatus;
import com.sunny.backend.competition.domain.CompetitionStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class FriendCompetition extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "friend_id")
	private Friend friend;

	@ManyToOne
	@JoinColumn(name = "competition_id")
	private Competition competition;

	@Enumerated(EnumType.STRING)
	private FriendCompetitionStatus friendCompetitionStatus;

	@Enumerated(value = EnumType.STRING)
	private CompetitionOutputStatus competitionOutputStatus; // 결과

	@Enumerated(value = EnumType.STRING)
	private CompetitionStatus competitionStatus;

	private FriendCompetition(
		Friend friend,
		Competition competition,
		FriendCompetitionStatus friendCompetitionStatus,
		CompetitionOutputStatus competitionOutputStatus
	) {
		this.friend = friend;
		this.competition = competition;
		this.friendCompetitionStatus = friendCompetitionStatus;
		this.competitionOutputStatus = competitionOutputStatus;
	}

	public static List<FriendCompetition> of(
		Friend fromFriend,
		Friend toFriend,
		String message,
		LocalDate startDate,
		LocalDate endDate,
		Long price,
		String compensation
	) {
		List<FriendCompetition> friendCompetitions = new ArrayList<>();
		Competition competition = Competition.of(message, startDate, endDate, price, compensation);
		friendCompetitions.add(
			new FriendCompetition(fromFriend, competition, FriendCompetitionStatus.SEND, CompetitionOutputStatus.DRAW));
		friendCompetitions.add(
			new FriendCompetition(toFriend, competition, FriendCompetitionStatus.RECEIVE,
				CompetitionOutputStatus.DRAW));
		return friendCompetitions;
	}

	public void validateIsCompeting() {
		if (friendCompetitionStatus == FriendCompetitionStatus.SEND) {
			throw new CustomException(COMPETITION_SEND);
		} else if (friendCompetitionStatus == FriendCompetitionStatus.RECEIVE) {
			throw new CustomException(COMPETITION_RECEIVE);
		} else if (friendCompetitionStatus == FriendCompetitionStatus.PROCEEDING) {
			throw new CustomException(COMPETITION_EXIST);
		}
	}

	public boolean isCompeting() {
		return (friendCompetitionStatus == FriendCompetitionStatus.SEND
			|| friendCompetitionStatus == FriendCompetitionStatus.RECEIVE
			|| friendCompetitionStatus == FriendCompetitionStatus.PROCEEDING);
	}

	public boolean isFriendCompetitionStatus(FriendCompetitionStatus friendCompetitionStatus) {
		return this.friendCompetitionStatus == friendCompetitionStatus;
	}

	public boolean isCompetitionOutputStatus(CompetitionOutputStatus competitionOutputStatus) {
		return this.competitionOutputStatus == competitionOutputStatus;
	}

	public void updateFriendCompetitionStatus(FriendCompetitionStatus friendCompetitionStatus) {
		this.friendCompetitionStatus = friendCompetitionStatus;
	}
}
