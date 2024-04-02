package com.sunny.backend.competition.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.competition.domain.Competition;
import com.sunny.backend.competition.domain.CompetitionStatus;
import com.sunny.backend.competition.dto.request.CompetitionRequest;
import com.sunny.backend.competition.dto.response.CompetitionResponse;
import com.sunny.backend.competition.dto.response.CompetitionStatusResponse;
import com.sunny.backend.competition.repository.CompetitionRepository;
import com.sunny.backend.consumption.repository.ConsumptionRepository;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.exception.FriendErrorCode;
import com.sunny.backend.friends.repository.FriendRepository;
import com.sunny.backend.notification.domain.CompetitionNotification;
import com.sunny.backend.notification.domain.Notification;
import com.sunny.backend.notification.dto.request.NotificationPushRequest;
import com.sunny.backend.notification.repository.CompetitionNotificationRepository;
import com.sunny.backend.notification.repository.NotificationRepository;
import com.sunny.backend.notification.service.NotificationService;
import com.sunny.backend.user.domain.Users;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompetitionService {

	private final ResponseService responseService;
	private final CompetitionRepository competitionRepository;
	private final FriendRepository friendRepository;
	private final ConsumptionRepository consumptionRepository;
	private final NotificationService notificationService;
	private final CompetitionNotificationRepository competitionNotificationRepository;
	private final NotificationRepository notificationRepository;

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<CompetitionResponse>> applyCompetition(
		CustomUserPrincipal customUserPrincipal, CompetitionRequest competitionRequest) {
		Friend friend = friendRepository.getById(competitionRequest.friendsId());
		friend.validateUser(customUserPrincipal.getUsers().getId());

		Friend friendWithUserFriend = friendRepository.findByUsersAndUserFriend(friend.getUserFriend(),
				friend.getUsers())
			.orElseThrow(() -> new CustomException(FriendErrorCode.FRIEND_NOT_FOUND));
		friendWithUserFriend.validateCompetitionStatus();

		Competition competition = Competition.of(
			competitionRequest.message(),
			competitionRequest.day(),
			competitionRequest.price(),
			competitionRequest.compensation(),
			friend.getUsers()
		);
		competitionRepository.save(competition);
		friend.addCompetition(competition);
		friendWithUserFriend.addCompetition(competition);

		CompetitionResponse competitionResponse = CompetitionResponse.from(friendWithUserFriend);

		String title = "[SUNNY] " + friend.getUsers().getNickname();
		String body = "님으로부터 대결 신청을 받았어요.";
		String bodyTitle = "대결 신청을 받았어요!";
		sendNotifications(title, bodyTitle, body, friend, competition);
		return responseService.getSingleResponse(HttpStatus.OK.value(), competitionResponse,
			"대결 신청이 됐습니다.");
	}

	private void sendNotifications(String title, String body, String bodyTitle, Friend friend,
		Competition competition) {
		Long postAuthor = friend.getUserFriend().getId();
		CompetitionNotification competitionNotification = CompetitionNotification.builder()
			.users(friend.getUserFriend()) //상대방꺼
			.friend(friend.getUsers())
			.competition(competition)
			.title(bodyTitle)
			.body(body)
			.name(friend.getUsers().getNickname())
			.createdAt(LocalDateTime.now())
			.build();
		competitionNotificationRepository.save(competitionNotification);
		List<Notification> notificationList = notificationRepository.findByUsers_Id(postAuthor);
		String notificationBody=friend.getUsers().getNickname()+body;
		System.out.println(notificationBody);
		if (notificationList.size() != 0) {
			NotificationPushRequest notificationPushRequest = new NotificationPushRequest(
				postAuthor,
				notificationBody,
				bodyTitle
			);
			notificationService.sendNotificationToFriends(title, notificationPushRequest);
		}
	}

	@Transactional
	public void acceptCompetition(CustomUserPrincipal customUserPrincipal, Long friendId) {
		Friend friendWithUser = friendRepository.getById(friendId);
		friendWithUser.validateUser(customUserPrincipal.getUsers().getId());

		Competition competition = competitionRepository.getById(friendWithUser.getCompetition().getId());
		competition.validateReceiveUser(friendWithUser.getUsers().getId());

		competition.updateStatus(CompetitionStatus.PROCEEDING);
		competition.addDate(LocalDate.now().plusDays(1),
			LocalDate.now().plusDays(1).plusDays(competition.getPeriod()));
		Friend friendWithUserFriend = friendRepository
			.findByUsersAndUserFriend(friendWithUser.getUserFriend(), friendWithUser.getUsers())
			.orElseThrow(() -> new CustomException(FriendErrorCode.FRIEND_NOT_FOUND));
		friendWithUserFriend.addCompetition(competition);

		String title = "[SUNNY] " + friendWithUser.getUsers().getNickname();
		String body = "님이 대결을 수락했어요";
		String bodyTitle = "대결 신청에 대한 응답을 받았어요";
		sendNotifications(title, bodyTitle, body, friendWithUser, competition);
	}

	@Transactional
	public void refuseFriend(CustomUserPrincipal customUserPrincipal, Long friendId) {
		Friend friend = friendRepository.getById(friendId);
		friend.validateUser(customUserPrincipal.getUsers().getId());

		Competition competition = competitionRepository.getById(friend.getCompetition().getId());
		competition.validateReceiveUser(friend.getUsers().getId());
		competition.updateStatus(CompetitionStatus.NONE);
		friendRepository.updateCompetitionToNull(competition.getId());

		String title = "[SUNNY] " + friend.getUsers().getNickname();
		String body ="님이 대결을 거절했어요";
		String bodyTitle = "대결 신청에 대한 응답을 받았어요";
		sendNotifications(title, bodyTitle, body, friend, competition);
	}

	@Transactional
	public void giveUpCompetition(CustomUserPrincipal customUserPrincipal, Long friendId) {
		Friend friend = friendRepository.getById(friendId);
		friend.validateUser(customUserPrincipal.getUsers().getId());

		Competition competition = competitionRepository.getById(friend.getCompetition().getId());
		competition.updateStatus(CompetitionStatus.GIVE_UP);
		friendRepository.updateCompetitionToNull(competition.getId());
	}

	public ResponseEntity<CommonResponse.SingleResponse<CompetitionResponse>> getCompetition(
		CustomUserPrincipal customUserPrincipal, Long friendId) {
		Friend friend = friendRepository.getById(friendId);
		friend.validateUser(customUserPrincipal.getUsers().getId());
		friend.validateCompetition();

		return responseService.getSingleResponse(HttpStatus.OK.value(), CompetitionResponse.from(friend), "결과 조회");
	}

	@Transactional
	// @Scheduled(cron = "*/30 * * * * *")
	public void sendCompetitionResult() {
		// for (CompetitionResultDto competitionResultDto : friendRepository.getCompetitionResult()) {
		// 	System.out.println(CompetitionResult.from(competitionResultDto));
		// 		messagingTemplate.convertAndSend("/sub/user/" + competitionResult.userId(), competitionResult);
		// }
		// competitionRepository.deleteByEndDate(LocalDate.now());
	}

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<CompetitionStatusResponse>> getCompetitionStatus(
		CustomUserPrincipal customUserPrincipal, Long friendId) {
		Friend friendWithUser = friendRepository.getById(friendId);
		friendWithUser.validateUser(customUserPrincipal.getUsers().getId());
		friendWithUser.validateCompetition();
		Competition competition = friendWithUser.getCompetition();

		Users user = friendWithUser.getUsers();
		Users userFriend = friendWithUser.getUserFriend();

		long diff = Duration.between(competition.getStartDate().atStartOfDay(),
			competition.getEndDate().atStartOfDay()).toDays();

		double percentageUsed = calculateUserPercentage(user.getId(), competition);
		double friendsPercentageUsed = calculateUserPercentage(userFriend.getId(), competition);

		competition.getOutput().updateOutput(percentageUsed, friendsPercentageUsed, user.getId(), userFriend.getId());

		CompetitionStatusResponse competitionStatus = CompetitionStatusResponse.builder()
			.competitionId(competition.getId())
			.price(competition.getPrice())
			.compensation(competition.getCompensation())
			.endDate(competition.getEndDate())
			.day(diff)
			.username(user.getNickname())
			.friendName(userFriend.getNickname())
			.userPercent(percentageUsed)
			.friendsPercent(friendsPercentageUsed)
			.build();
		return responseService.getSingleResponse(HttpStatus.OK.value(), competitionStatus, "결과 조회");
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