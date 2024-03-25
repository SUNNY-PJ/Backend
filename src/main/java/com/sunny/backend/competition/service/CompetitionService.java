package com.sunny.backend.competition.service;

import static com.sunny.backend.consumption.domain.QConsumption.consumption;

import com.sunny.backend.consumption.domain.Consumption;
import com.sunny.backend.save.domain.Save;
import com.sunny.backend.save.dto.response.SaveResponse;
import com.sunny.backend.save.dto.response.SaveResponse.SaveListResponse;
import com.sunny.backend.save.repository.SaveRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.competition.domain.Competition;
import com.sunny.backend.competition.domain.CompetitionStatus;
import com.sunny.backend.competition.dto.request.CompetitionRequest;
import com.sunny.backend.competition.dto.response.CompetitionApplyResponse;
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
	private final SimpMessagingTemplate messagingTemplate;
	private final SaveRepository saveRepository;

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<CompetitionApplyResponse>> applyCompetition(
			CustomUserPrincipal customUserPrincipal, CompetitionRequest competitionRequest) {
		Friend friend = friendRepository.getById(competitionRequest.friendsId());
		friend.validateUser(customUserPrincipal.getUsers().getId());
		friend.validateCompetitionStatus();

		Friend friendWithUserFriend = friendRepository.findByUsersAndUserFriend(friend.getUserFriend(),
						friend.getUsers())
				.orElseThrow(() -> new CustomException(FriendErrorCode.FRIEND_NOT_FOUND));

		Competition competition = Competition.of(
				competitionRequest.message(),
				competitionRequest.day(),
				competitionRequest.price(),
				competitionRequest.compensation()
		);
		competitionRepository.save(competition);
		friendWithUserFriend.addCompetition(competition);

		CompetitionApplyResponse competitionApplyResponse = CompetitionApplyResponse.of(
				friendWithUserFriend.getId(),
				friendWithUserFriend.getUsers().getNickname(), competition);
		sendNotifications(friendWithUserFriend, competition);
		//  신청후 알람을 보내는 행위
		return responseService.getSingleResponse(HttpStatus.OK.value(), competitionApplyResponse,
				"대결 신청이 됐습니다.");
	}

	private void sendNotifications(Friend friend, Competition competition) {
		Long postAuthor = friend.getUserFriend().getId();
		String title = "[SUNNY] " + friend.getUsers().getNickname();
		String bodyTitle = friend.getUsers().getNickname() + "님이 대결을 신청했어요";
		String body = competition.getMessage();

		CompetitionNotification competitionNotification = CompetitionNotification.builder()
				.users(friend.getUserFriend()) //상대방꺼
				.competition(competition)
				.title(bodyTitle)
				.body(body)
				.name(friend.getUsers().getNickname())
				.createdAt(LocalDateTime.now())
				.build();
		competitionNotificationRepository.save(competitionNotification);
		List<Notification> notificationList = notificationRepository.findByUsers_Id(postAuthor);

		if (notificationList.size() != 0) {
			NotificationPushRequest notificationPushRequest = new NotificationPushRequest(
					postAuthor,
					bodyTitle,
					body
			);
			notificationService.sendNotificationToFriends(title, notificationPushRequest);
		}
	}

	@Transactional
	public void acceptCompetition(CustomUserPrincipal customUserPrincipal, Long friendId) {
		Friend friendWithUser = friendRepository.getById(friendId);
		friendWithUser.validateUser(customUserPrincipal.getUsers().getId());

		Competition competition = competitionRepository.getById(
				friendWithUser.getCompetition().getId());
		competition.updateStatus(CompetitionStatus.PROCEEDING);
		competition.addDate(LocalDate.now().plusDays(1),
				LocalDate.now().plusDays(1).plusDays(competition.getPeriod()));
		Friend friendWithUserFriend = friendRepository
				.findByUsersAndUserFriend(friendWithUser.getUserFriend(), friendWithUser.getUsers())
				.orElseThrow(() -> new CustomException(FriendErrorCode.FRIEND_NOT_FOUND));
		friendWithUserFriend.addCompetition(competition);
	}

	@Transactional
	public void refuseFriend(CustomUserPrincipal customUserPrincipal, Long friendId) {
		Friend friendWithUser = friendRepository.getById(friendId);
		friendWithUser.validateUser(customUserPrincipal.getUsers().getId());

		Competition competition = competitionRepository.getById(
				friendWithUser.getCompetition().getId());
		competitionRepository.deleteById(competition.getId());
		friendWithUser.addCompetition(null);
	}

	//	@Transactional
//	public ResponseEntity<CommonResponse.SingleResponse<CompetitionStatusResponse>> getCompetitionStatus(
//			CustomUserPrincipal customUserPrincipal, Long friendId) {
//		Friend friendWithUser = friendRepository.getById(friendId);
//		friendWithUser.validateUser(customUserPrincipal.getUsers().getId());
//
//		Competition competition = competitionRepository.getById(friendWithUser.getCompetition().getId());
//
//		Users user = friendWithUser.getUsers();
//		Users userFriend = friendWithUser.getUserFriend();
//
//		long diff = Duration.between(competition.getStartDate().atStartOfDay(),
//				competition.getEndDate().atStartOfDay()).toDays();
//
//		List<Save> userSaves = saveRepository.findAllByUsers_Id(user.getId());
//		List<Save> friendsSaves = saveRepository.findAllByUsers_Id(userFriend.getId());
//
//		double percentageUsed;
//		double friendsPercentageUsed;
//
//		if (userSaves != null && !userSaves.isEmpty()) {
//			double userPercentageSum = 0.0;
//			for (Save userSave : userSaves) {
//				Long userMoney = consumptionRepository.getComsumptionMoney(user.getId(), userSave.getStartDate(), userSave.getEndDate());
//				double percentage = userSave.calculateSavePercentage(userMoney, userSave);
//				userPercentageSum += percentage;
//			}
//			percentageUsed = userPercentageSum / userSaves.size();
//		} else {
//			percentageUsed = 100.0;
//		}
//
//		if (friendsSaves != null && !friendsSaves.isEmpty()) {
//			double friendsPercentageSum = 0.0;
//			for (Save friendSave : friendsSaves) {
//				Long friendsMoney = consumptionRepository.getComsumptionMoney(userFriend.getId(), friendSave.getStartDate(), friendSave.getEndDate());
//				double friendsPercentage = friendSave.calculateSavePercentage(friendsMoney, friendSave);
//				friendsPercentageSum += friendsPercentage;
//			}
//			friendsPercentageUsed = friendsPercentageSum / friendsSaves.size();
//		} else {
//			friendsPercentageUsed = 100.0;
//		}
//
//		String result;
//		if (percentageUsed > friendsPercentageUsed) {
//			if (diff < 0) {
//				competition.updateOutput(user.getId());
//			}
//			result = "유저가 이기고 있습니다.";
//		} else if (percentageUsed < friendsPercentageUsed) {
//			if (diff < 0) {
//				competition.updateOutput(userFriend.getId());
//			}
//			result = "유저가 지고 있습니다.";
//		} else {
//			if (diff < 0) {
//				competition.updateOutput(-1L);
//			}
//			result = "비기고 있습니다.";
//		}
//
//		CompetitionStatusResponse competitionStatus = CompetitionStatusResponse.builder()
//				.competitionId(competition.getId())
//				.price(competition.getPrice())
//				.compensation(competition.getCompensation())
//				.endDate(competition.getEndDate())
//				.dDay(diff)
//				.username(user.getNickname())
//				.friendName(userFriend.getNickname())
//				.userPercent(percentageUsed)
//				.friendsPercent(friendsPercentageUsed)
//				.result(result)
//				.build();
//		return responseService.getSingleResponse(HttpStatus.OK.value(), competitionStatus, "결과 조회");
//	}
	@Transactional
	public void giveUpCompetition(CustomUserPrincipal customUserPrincipal, Long friendId) {
		Friend friend = friendRepository.getById(friendId);
		friend.validateUser(customUserPrincipal.getUsers().getId());

		Competition competition = competitionRepository.getById(friend.getCompetition().getId());
		competition.updateStatus(CompetitionStatus.GIVE_UP);
		friendRepository.updateNullCompetition(competition.getId());
	}

	public ResponseEntity<CommonResponse.ListResponse<CompetitionResponse>> getCompetition(
			CustomUserPrincipal customUserPrincipal) {
		List<CompetitionResponse> responses = friendRepository.findByUsers(
						customUserPrincipal.getUsers())
				.stream()
				.filter(friend -> friend.getCompetition() != null)
				.map(friend -> CompetitionResponse.of(friend.getUserFriend().getId(),
						friend.getCompetition()))
				.toList();
		return responseService.getListResponse(HttpStatus.OK.value(), responses, "결과 조회");
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

		Competition competition = competitionRepository.getById(
				friendWithUser.getCompetition().getId());

		Users user = friendWithUser.getUsers();
		Users userFriend = friendWithUser.getUserFriend();

		long diff = Duration.between(competition.getStartDate().atStartOfDay(),
				competition.getEndDate().atStartOfDay()).toDays();

		List<Consumption> userSaves = consumptionRepository.findByUsersId(user.getId());
		List<Consumption> friendsSaves = consumptionRepository.findByUsersId(userFriend.getId());
		double percentageUsed = calculateUserPercentage(userSaves, user.getId(), competition);
		double friendsPercentageUsed = calculateUserPercentage(friendsSaves, userFriend.getId(),
				competition);

		CompetitionStatusResponse competitionStatus = CompetitionStatusResponse.builder()
				.competitionId(competition.getId())
				.price(competition.getPrice())
				.compensation(competition.getCompensation())
				.endDate(competition.getEndDate())
				.dDay(diff)
				.username(user.getNickname())
				.friendName(userFriend.getNickname())
				.userPercent(percentageUsed)
				.friendsPercent(friendsPercentageUsed)
				.build();
		return responseService.getSingleResponse(HttpStatus.OK.value(), competitionStatus, "결과 조회");
	}

	//TODO 메소드 분리
	private double calculateUserPercentage(List<Consumption> consumptions, Long userId,
			Competition competition) {
		if (consumptions == null) {
			return 100.0;
		}

		// 사용자 소비 금액 계산
		double totalSpent = consumptions.stream()
				.filter(consumption -> consumption.getUsers().getId().equals(userId))
				.mapToDouble(Consumption::getMoney)
				.sum();

		//소비 비율 계산
		double percentage = 100.0 - ((totalSpent / competition.getPrice()) * 100.0);
		return Math.round(percentage * 10) / 10.0; // 소수점 첫째 자리 반올림
	}
}