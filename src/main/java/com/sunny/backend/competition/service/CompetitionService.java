package com.sunny.backend.competition.service;

import com.sunny.backend.competition.domain.CompetitionStatus;
import com.sunny.backend.notification.domain.CompetitionNotification;
import com.sunny.backend.notification.domain.Notification;
import com.sunny.backend.notification.dto.request.NotificationPushRequest;
import com.sunny.backend.notification.repository.CompetitionNotificationRepository;
import com.sunny.backend.notification.repository.NotificationRepository;
import com.sunny.backend.notification.service.NotificationService;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.competition.dto.request.CompetitionRequest;
import com.sunny.backend.competition.dto.response.CompetitionApplyResponse;
import com.sunny.backend.competition.dto.response.CompetitionResponseDto;
import com.sunny.backend.competition.domain.Competition;
import com.sunny.backend.competition.repository.CompetitionRepository;
import com.sunny.backend.consumption.repository.ConsumptionRepository;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.exception.FriendErrorCode;
import com.sunny.backend.friends.repository.FriendRepository;
import com.sunny.backend.auth.jwt.CustomUserPrincipal;
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
	public ResponseEntity<CommonResponse.SingleResponse<CompetitionApplyResponse>> applyCompetition(CustomUserPrincipal customUserPrincipal,
		CompetitionRequest competitionRequest) throws IOException{
		Friend friendWithUser = friendRepository.getById(competitionRequest.friendsId());
		friendWithUser.validateFriendsByUser(friendWithUser.getUserFriend().getId(), customUserPrincipal.getUsers().getId());

		Friend friendWithUserFriend = friendRepository
			.findByUsers_IdAndUserFriend_Id(friendWithUser.getUserFriend().getId(), friendWithUser.getUsers().getId())
			.orElseThrow(() -> new CustomException(FriendErrorCode.FRIEND_NOT_FOUND));

		Competition competition = competitionRequest.toEntity();
		competitionRepository.save(competition);
		friendWithUserFriend.addCompetition(competition);

		CompetitionApplyResponse competitionApplyResponse =  CompetitionApplyResponse.of(friendWithUserFriend.getId(),
			friendWithUserFriend.getUsers().getNickname(), competition);
		sendNotifications(friendWithUserFriend,competition);
		//  신청후 알람을 보내는 행위
		return responseService.getSingleResponse(HttpStatus.OK.value(), competitionApplyResponse, "대결 신청이 됐습니다.");
	}

	private void sendNotifications(Friend friend,Competition competition) throws IOException {
		Long postAuthor=friend.getUserFriend().getId();
		System.out.println(postAuthor);
		String title="[SUNNY] "+friend.getUsers().getNickname();
		String bodyTitle="대결 신청을 받았어요";
		String body=competition.getCompensation();
		System.out.println(friend.getStatus());

		CompetitionNotification competitionNotification=CompetitionNotification.builder()
				.users(friend.getUserFriend()) //상대방꺼
				.competition(competition)
				.title(bodyTitle)
				.body(body)
				.name(friend.getUsers().getNickname())
				.createdAt(LocalDateTime.now())
				.build();
		competitionNotificationRepository.save(competitionNotification);
		List<Notification> notificationList=notificationRepository.findByUsers_Id(postAuthor);
		System.out.println(notificationList.size());

		if(notificationList.size()!=0) {
			NotificationPushRequest notificationPushRequest = new NotificationPushRequest(
					postAuthor,
					bodyTitle,
					body
			);
			System.out.println(notificationPushRequest.getPostAuthor());
			notificationService.sendNotificationToFriends(title,notificationPushRequest);
		}
	}

	@Transactional
	public void acceptCompetition(CustomUserPrincipal customUserPrincipal, Long friendId) {
		Friend friendWithUser = friendRepository.getById(friendId);
		friendWithUser.validateFriendsByUser(friendWithUser.getUsers().getId(), customUserPrincipal.getUsers().getId());

		Competition competition = competitionRepository.getById(friendWithUser.getCompetition().getId());
		competition.approveStatus();
		Friend friendWithUserFriend = friendRepository
			.findByUsers_IdAndUserFriend_Id(friendWithUser.getUserFriend().getId(), friendWithUser.getUsers().getId())
			.orElseThrow(() -> new CustomException(FriendErrorCode.FRIEND_NOT_FOUND));
		friendWithUserFriend.addCompetition(competition);
	}

	@Transactional
	public void refuseFriend(CustomUserPrincipal customUserPrincipal, Long friendId) {
		Friend friendWithUser = friendRepository.getById(friendId);
		friendWithUser.validateFriendsByUser(friendWithUser.getUsers().getId(), customUserPrincipal.getUsers().getId());

		Competition competition = competitionRepository.getById(friendWithUser.getCompetition().getId());
		competitionRepository.deleteById(competition.getId());
		friendWithUser.addCompetition(null);
	}

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<CompetitionResponseDto.CompetitionStatus>> getCompetitionStatus(
		CustomUserPrincipal customUserPrincipal, Long friendId) {
		Friend friendWithUser = friendRepository.getById(friendId);
		friendWithUser.validateFriendsByUser(friendWithUser.getUsers().getId(), customUserPrincipal.getUsers().getId());

		Competition competition = competitionRepository.getById(friendWithUser.getCompetition().getId());

		Users user = friendWithUser.getUsers();
		Users userFriend = friendWithUser.getUserFriend();


		long diff = Duration.between(competition.getStartDate().atStartOfDay(),
				competition.getEndDate().atStartOfDay()).toDays();

		// 날짜 간의 소비 금액 구하기 로직
		Long userMoney = consumptionRepository.getComsumptionMoney(user.getId(), competition.getStartDate(),
			competition.getEndDate());
		Long friendsMoney = consumptionRepository.getComsumptionMoney(user.getId(), competition.getStartDate(),
			competition.getEndDate());

		String result;
		if (userMoney > friendsMoney) {
			//이거 나중에 메소드로 한 번에 빼는게 좋을 듯
			if(diff<0){
				competition.updateOutput(CompetitionStatus.WIN);
			}
			result = "유저가 이기고 있습니다.";
		} else if (userMoney < friendsMoney) {
			if(diff<0){
				competition.updateOutput(CompetitionStatus.LOSE);
			}
			result = "유저가 지고 있습니다.";
		} else {
			if(diff<0){
				competition.updateOutput(CompetitionStatus.DRAW);
			}
			result = "비기고 있습니다.";
		}

		CompetitionResponseDto.CompetitionStatus competitionStatus = CompetitionResponseDto.CompetitionStatus.builder()
			.competitionId(competition.getId())
			.price(competition.getPrice())
			.compensation(competition.getCompensation())
			.endDate(competition.getEndDate())
			.dDay(diff)
			.username(user.getNickname())
			.friendName(userFriend.getNickname())
			.userPercent(userMoney / competition.getPrice())
			.friendsPercent(friendsMoney / competition.getPrice())
			.result(result)
			.build();
		return responseService.getSingleResponse(HttpStatus.OK.value(), competitionStatus, "결과 조회");
	}

}
