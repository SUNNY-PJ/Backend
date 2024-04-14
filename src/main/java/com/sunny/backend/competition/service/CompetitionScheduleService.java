package com.sunny.backend.competition.service;

import static com.sunny.backend.competition.exception.CompetitionErrorCode.*;

import java.time.LocalDate;

import javax.transaction.Transactional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.competition.domain.Competition;
import com.sunny.backend.competition.domain.CompetitionOutputStatus;
import com.sunny.backend.competition.repository.CompetitionRepository;
import com.sunny.backend.consumption.repository.ConsumptionRepository;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.domain.FriendCompetition;
import com.sunny.backend.friends.domain.FriendCompetitionStatus;
import com.sunny.backend.friends.exception.FriendErrorCode;
import com.sunny.backend.friends.repository.FriendCompetitionRepository;
import com.sunny.backend.friends.repository.FriendRepository;
import com.sunny.backend.notification.service.FriendNotiService;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.util.MathUtil;
import com.sunny.backend.util.SockMessageUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompetitionScheduleService {

	private final SockMessageUtil sockMessageUtil;
	private final CompetitionRepository competitionRepository;
	private final ConsumptionRepository consumptionRepository;
	private final FriendCompetitionRepository friendCompetitionRepository;
	private final FriendRepository friendRepository;
	private final FriendNotiService friendNotiService;

	@Scheduled(cron = "0 0 00 * * ?")
	@Transactional
	public void checkCompetition() {
		System.out.println("competition test!!!!!");
		for (Competition competition : competitionRepository.findByEndDate(LocalDate.now().minusDays(1))) {
			FriendCompetition friendCompetition = friendCompetitionRepository.findFirstByCompetition(competition);
			if (friendCompetition.isFriendCompetitionStatus(FriendCompetitionStatus.PROCEEDING)) {
				Users user = friendCompetition.getFriend().getUsers();
				Users userFriend = friendCompetition.getFriend().getUserFriend();

				Friend friendWithUserFriend = friendRepository.findByUsersAndUserFriend(userFriend, user)
					.orElseThrow(() -> new CustomException(FriendErrorCode.FRIEND_NOT_FOUND));
				FriendCompetition friendCompetitionUserFriend = friendCompetitionRepository.
					findFirstByFriendOrderByCreatedDateDesc(friendWithUserFriend)
					.orElseThrow(() -> new CustomException(COMPETITION_NOT_FOUND));

				Long userId = user.getId();
				Long userFriendId = userFriend.getId();
				LocalDate startDate = competition.getStartDate();
				LocalDate endDate = competition.getEndDate();
				Long userUsedMoney = consumptionRepository.getComsumptionMoney(userId, startDate, endDate);
				Long friendUsedMoney = consumptionRepository.getComsumptionMoney(userFriendId, startDate, endDate);

				double percentageUsed = MathUtil.calculatePercentage(userUsedMoney, competition.getPrice());
				double friendsPercentageUsed = MathUtil.calculatePercentage(friendUsedMoney, competition.getPrice());
				System.out.println("competition test2222222!!!!!");
				String bodyTitle = "대결 결과를 알려드려요";
				String winBody = "님과의 대결에서 승리했어요!";
				String loseBody = "님과의 대결에서 패배했어요!";
				String drawBody = "님과의 대결에서 비겼어요!";
				String winTitle = "[SUNNY] " + user.getNickname();
				String loseTitle = "[SUNNY] " + userFriend.getNickname();

				if (friendsPercentageUsed < percentageUsed) {
					friendCompetition.updateCompetitionOutputStatus(CompetitionOutputStatus.WIN);
					friendCompetitionUserFriend.updateCompetitionOutputStatus(CompetitionOutputStatus.LOSE);

					friendNotiService.sendCompetitionNotifications(loseTitle, winBody, bodyTitle, user, userFriend,
						friendCompetitionUserFriend);
					friendNotiService.sendCompetitionNotifications(winTitle, loseBody, bodyTitle, userFriend, user,
						friendCompetitionUserFriend);
					sockMessageUtil.sendCompetitionUserWinner(user, userFriend, competition);
				} else if (friendsPercentageUsed > percentageUsed) {
					friendCompetition.updateCompetitionOutputStatus(CompetitionOutputStatus.LOSE);
					friendCompetitionUserFriend.updateCompetitionOutputStatus(CompetitionOutputStatus.WIN);

					friendNotiService.sendCompetitionNotifications(winTitle, winBody, bodyTitle, userFriend, user,
						friendCompetitionUserFriend);
					friendNotiService.sendCompetitionNotifications(loseTitle, loseBody, bodyTitle, user, userFriend,
						friendCompetitionUserFriend);
					sockMessageUtil.sendCompetitionUserWinner(userFriend, user, competition);
				} else {
					friendCompetition.updateCompetitionOutputStatus(CompetitionOutputStatus.DRAW);
					friendCompetitionUserFriend.updateCompetitionOutputStatus(CompetitionOutputStatus.DRAW);

					friendNotiService.sendCompetitionNotifications(winTitle, drawBody, bodyTitle, userFriend, user,
						friendCompetitionUserFriend);
					friendNotiService.sendCompetitionNotifications(loseTitle, drawBody, bodyTitle, user, userFriend,
						friendCompetitionUserFriend);
					sockMessageUtil.sendCompetitionDraw(user, userFriend, competition);
				}

				friendCompetition.updateFriendCompetitionStatus(FriendCompetitionStatus.COMPLETE);
				friendCompetitionUserFriend.updateFriendCompetitionStatus(FriendCompetitionStatus.COMPLETE);
			}
		}
	}
}
