package com.sunny.backend.friends.domain;

import static com.sunny.backend.competition.exception.CompetitionErrorCode.*;
import static lombok.AccessLevel.*;

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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@ToString
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

	public static FriendCompetition of(
		Friend friend,
		Competition competition,
		FriendCompetitionStatus friendCompetitionStatus,
		CompetitionOutputStatus competitionOutputStatus
	) {
		return new FriendCompetition(friend, competition, friendCompetitionStatus, competitionOutputStatus);
	}

	// public static List<FriendCompetition> of(
	// 	Friend fromFriend,
	// 	Friend toFriend,
	// 	Competition competition
	// ) {
	// 	List<FriendCompetition> friendCompetitions = new ArrayList<>();
	// 	friendCompetitions.add(
	// 		new FriendCompetition(fromFriend, competition, FriendCompetitionStatus.SEND, CompetitionOutputStatus.NONE));
	// 	friendCompetitions.add(
	// 		new FriendCompetition(toFriend, competition, FriendCompetitionStatus.RECEIVE,
	// 			CompetitionOutputStatus.NONE));
	// 	return friendCompetitions;
	// }

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

	public void updateFriendCompetitionStatus(FriendCompetitionStatus friendCompetitionStatus) {
		this.friendCompetitionStatus = friendCompetitionStatus;
	}

	public void updateCompetitionOutputStatus(CompetitionOutputStatus competitionOutputStatus) {
		this.competitionOutputStatus = competitionOutputStatus;
	}
}
