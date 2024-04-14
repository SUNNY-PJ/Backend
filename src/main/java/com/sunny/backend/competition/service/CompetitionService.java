package com.sunny.backend.competition.service;

import static com.sunny.backend.competition.exception.CompetitionErrorCode.*;

import java.time.Duration;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.competition.domain.Competition;
import com.sunny.backend.competition.domain.CompetitionOutputStatus;
import com.sunny.backend.competition.dto.request.CompetitionRequest;
import com.sunny.backend.competition.dto.response.CompetitionStatusResponse;
import com.sunny.backend.competition.repository.CompetitionRepository;
import com.sunny.backend.consumption.repository.ConsumptionRepository;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.domain.FriendCompetition;
import com.sunny.backend.friends.domain.FriendCompetitionStatus;
import com.sunny.backend.friends.dto.response.FriendCompetitionResponses;
import com.sunny.backend.friends.exception.FriendErrorCode;
import com.sunny.backend.friends.repository.FriendCompetitionRepository;
import com.sunny.backend.friends.repository.FriendRepository;
import com.sunny.backend.notification.service.FriendNotiService;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompetitionService {
	private final CompetitionRepository competitionRepository;
	private final FriendRepository friendRepository;
	private final ConsumptionRepository consumptionRepository;
	private final FriendNotiService friendNotiService;
	private final UserRepository userRepository;
	private final FriendCompetitionRepository friendCompetitionRepository;
	private final ResponseService responseService;

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<FriendCompetitionResponses>> applyCompetition(
		CustomUserPrincipal customUserPrincipal,
		CompetitionRequest competitionRequest
	) {
		Users users = userRepository.getById(customUserPrincipal.getId());
		Friend friendWithUser = friendRepository.getById(competitionRequest.friendsId());
		friendWithUser.validateUser(users.getId());

		// 이미 대결 신청을 보냈거나 받았거나 진행중인 경우 에러 발생
		friendCompetitionRepository.findFirstByFriendOrderByCreatedDateDesc(friendWithUser)
			.ifPresent(FriendCompetition::validateIsCompeting);

		Friend friendWithUserFriend = friendRepository.findByUsersAndUserFriend(friendWithUser.getUserFriend(),
				friendWithUser.getUsers())
			.orElseThrow(() -> new CustomException(FriendErrorCode.FRIEND_NOT_FOUND));

		// 대결을 받은 사람이 신청한 사람과 경쟁중이 아닌데 경쟁중 상태로 뜨는 경우, 서버 이슈라 생각하고 해당 경쟁을 삭제
		friendCompetitionRepository.findFirstByFriendOrderByCreatedDateDesc(friendWithUserFriend)
			.ifPresent(friendCompetition -> {
				if (friendCompetition.isCompeting()) {
					friendCompetitionRepository.delete(friendCompetition);
					competitionRepository.delete(friendCompetition.getCompetition());
				}
			});

		Competition competition = Competition.of(
			competitionRequest.message(),
			competitionRequest.startDate(),
			competitionRequest.endDate(),
			competitionRequest.price(),
			competitionRequest.compensation()
		);
		competitionRepository.save(competition);

		FriendCompetition friendCompetition = FriendCompetition.of(friendWithUser, competition,
			FriendCompetitionStatus.SEND,
			CompetitionOutputStatus.NONE);
		friendCompetitionRepository.save(friendCompetition);

		FriendCompetition friendCompetitionToUserFriend = FriendCompetition.of(friendWithUserFriend, competition,
			FriendCompetitionStatus.RECEIVE,
			CompetitionOutputStatus.NONE);
		friendCompetitionRepository.save(friendCompetitionToUserFriend);

		String title = "[SUNNY] " + friendWithUser.getUsers().getNickname();
		String body = "님으로부터 대결 신청을 받았어요.";
		String bodyTitle = "대결 신청을 받았어요!";
		friendNotiService.sendCompetitionNotifications(title, body, bodyTitle, friendWithUser.getUserFriend(),
			friendWithUser.getUsers(),
			friendCompetitionToUserFriend);
		return responseService.getSingleResponse(HttpStatus.OK.value(),
			FriendCompetitionResponses.from(friendCompetition), "");
	}

	public Friend updateCompetitionReturnFriend(
		Long userId,
		Long friendId,
		FriendCompetitionStatus userStatus,
		FriendCompetitionStatus userFriendStatus
	) {
		Users users = userRepository.getById(userId);
		Friend friendWithUser = friendRepository.getById(friendId);
		friendWithUser.validateUser(users.getId());

		FriendCompetition friendCompetition = friendCompetitionRepository.findFirstByFriendOrderByCreatedDateDesc(
				friendWithUser)
			.orElseThrow(() -> new CustomException(COMPETITION_NOT_FOUND));
		if (!friendCompetition.isFriendCompetitionStatus(FriendCompetitionStatus.RECEIVE)) {
			throw new CustomException(COMPETITION_NOT_RECEIVE);
		}
		friendCompetition.updateFriendCompetitionStatus(userStatus);

		Friend friendWithUserFriend = friendRepository.findByUsersAndUserFriend(friendWithUser.getUserFriend(),
				friendWithUser.getUsers())
			.orElseThrow(() -> new CustomException(FriendErrorCode.FRIEND_NOT_FOUND));
		FriendCompetition friendCompetitionUserFriend = friendCompetitionRepository.findFirstByFriendOrderByCreatedDateDesc(
				friendWithUserFriend)
			.orElseThrow(() -> new CustomException(COMPETITION_NOT_FOUND));
		// 대결을 받은 사람은 있는데 신청자가 없는 경우, 해당 대결을 삭제
		if (!friendCompetitionUserFriend.isFriendCompetitionStatus(FriendCompetitionStatus.SEND)) {
			friendCompetitionRepository.delete(friendCompetition);
			friendCompetitionRepository.delete(friendCompetitionUserFriend);
			competitionRepository.delete(friendCompetition.getCompetition());
			throw new CustomException(COMPETITION_SERVER_ERROR);
		}
		friendCompetitionUserFriend.updateFriendCompetitionStatus(userFriendStatus);

		return friendWithUser;
	}

	@Transactional
	public void acceptCompetition(CustomUserPrincipal customUserPrincipal, Long friendId) {
		Friend friendWithUser = updateCompetitionReturnFriend(customUserPrincipal.getId(), friendId,
			FriendCompetitionStatus.PROCEEDING, FriendCompetitionStatus.PROCEEDING);

		//TODO title,body,bodyTitle 분리
		String title = "[SUNNY] " + friendWithUser.getUsers().getNickname();
		String body = "님이 대결을 수락했어요";
		String bodyTitle = "대결 신청에 대한 응답을 받았어요";

		friendNotiService.sendNotifications(title, body, bodyTitle, friendWithUser);
	}

	@Transactional
	public void refuseFriend(CustomUserPrincipal customUserPrincipal, Long friendId) {
		Friend friendWithUser = updateCompetitionReturnFriend(customUserPrincipal.getId(), friendId,
			FriendCompetitionStatus.REFUSE, FriendCompetitionStatus.NONE);

		String title = "[SUNNY] " + friendWithUser.getUsers().getNickname();
		String body = "님이 대결을 거절했어요";
		String bodyTitle = "대결 신청에 대한 응답을 받았어요";
		friendNotiService.sendNotifications(title, body, bodyTitle, friendWithUser);
	}

	//TODO 대결 포기 배너 알림 필요 여부 논의 & 추가
	@Transactional
	public void giveUpCompetition(CustomUserPrincipal customUserPrincipal, Long friendId) {
		Users users = userRepository.getById(customUserPrincipal.getId());
		Friend friendWithUser = friendRepository.getById(friendId);
		friendWithUser.validateUser(users.getId());

		FriendCompetition friendCompetition = friendCompetitionRepository.findFirstByFriendOrderByCreatedDateDesc(
				friendWithUser)
			.orElseThrow(() -> new CustomException(COMPETITION_NOT_FOUND));
		if (!friendCompetition.isCompeting()) {
			throw new CustomException(COMPETITION_NOT_FOUND);
		}
		friendCompetition.updateFriendCompetitionStatus(FriendCompetitionStatus.GIVE_UP);

		Friend friendWithUserFriend = friendRepository.findByUsersAndUserFriend(friendWithUser.getUserFriend(),
				friendWithUser.getUsers())
			.orElseThrow(() -> new CustomException(FriendErrorCode.FRIEND_NOT_FOUND));
		FriendCompetition friendCompetitionUserFriend = friendCompetitionRepository.findFirstByFriendOrderByCreatedDateDesc(
				friendWithUserFriend)
			.orElseThrow(() -> new CustomException(COMPETITION_NOT_FOUND));
		friendCompetitionUserFriend.updateFriendCompetitionStatus(FriendCompetitionStatus.NONE);
	}

	public ResponseEntity<CommonResponse.ListResponse<FriendCompetitionResponses>> getCompetition(
		CustomUserPrincipal customUserPrincipal,
		Long friendId,
		Long competitionId
	) {
		Users users = userRepository.getById(customUserPrincipal.getId());
		Friend friend = friendRepository.getById(friendId);
		friend.validateUser(users.getId());

		return responseService.getListResponse(HttpStatus.OK.value(),
			friendCompetitionRepository.getByFriendAndCompetition(friendId, competitionId)
				.stream()
				.map(FriendCompetitionResponses::from)
				.toList(),
			"");
	}

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<CompetitionStatusResponse>> getCompetitionStatus(
		CustomUserPrincipal customUserPrincipal,
		Long friendId,
		Long competitionId
	) {
		Users users = userRepository.getById(customUserPrincipal.getId());
		Friend friendWithUser = friendRepository.getById(friendId);
		friendWithUser.validateUser(users.getId());

		Competition competition = competitionRepository.getById(competitionId);

		FriendCompetition friendCompetition = friendCompetitionRepository.findByFriendAndCompetition(friendWithUser,
				competition)
			.orElseThrow(() -> new CustomException(COMPETITION_NOT_FOUND));
		Friend friendWithUserFriend = friendRepository.findByUsersAndUserFriend(friendWithUser.getUserFriend(),
				friendWithUser.getUsers())
			.orElseThrow(() -> new CustomException(FriendErrorCode.FRIEND_NOT_FOUND));
		FriendCompetition friendCompetitionUserFriend = friendCompetitionRepository.findByFriendAndCompetition(
				friendWithUserFriend, competition)
			.orElseThrow(() -> new CustomException(COMPETITION_NOT_FOUND));

		Users user = friendWithUser.getUsers();
		Users userFriend = friendWithUser.getUserFriend();
		long diff = Duration.between(competition.getStartDate().atStartOfDay(),
			competition.getEndDate().atStartOfDay()).toDays();
		double percentageUsed = calculateUserPercentage(user.getId(), competition);
		double friendsPercentageUsed = calculateUserPercentage(userFriend.getId(), competition);

		if (friendsPercentageUsed < percentageUsed) {
			friendCompetition.updateCompetitionOutputStatus(CompetitionOutputStatus.WIN);
			friendCompetitionUserFriend.updateCompetitionOutputStatus(CompetitionOutputStatus.LOSE);
		} else if (friendsPercentageUsed > percentageUsed) {
			friendCompetition.updateCompetitionOutputStatus(CompetitionOutputStatus.LOSE);
			friendCompetitionUserFriend.updateCompetitionOutputStatus(CompetitionOutputStatus.WIN);
		} else {
			friendCompetition.updateCompetitionOutputStatus(CompetitionOutputStatus.DRAW);
			friendCompetitionUserFriend.updateCompetitionOutputStatus(CompetitionOutputStatus.DRAW);
		}

		return responseService.getSingleResponse(HttpStatus.OK.value(), CompetitionStatusResponse.builder()
			.competitionId(competition.getId())
			.price(competition.getPrice())
			.compensation(competition.getCompensation())
			.endDate(competition.getEndDate())
			.day(diff)
			.username(user.getNickname())
			.friendName(userFriend.getNickname())
			.userPercent(percentageUsed)
			.friendsPercent(friendsPercentageUsed)
			.build(), "");
	}

	//TODO 메소드 분리
	public double calculateUserPercentage(Long userId, Competition competition) {
		Long totalSpent = consumptionRepository.getComsumptionMoney(userId, competition.getStartDate(),
			competition.getEndDate());
		if (totalSpent == null) {
			return 100.0;
		}

		double percentage = 100.0 - ((totalSpent * 100.0) / competition.getPrice());
		return Math.round(percentage * 10) / 10.0; // 소수점 첫째 자리 반올림
	}
}