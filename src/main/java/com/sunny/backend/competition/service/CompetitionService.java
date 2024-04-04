package com.sunny.backend.competition.service;

import java.time.Duration;
import java.time.LocalDate;

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
import com.sunny.backend.notification.service.FriendNotiService;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompetitionService {
	private final ResponseService responseService;
	private final CompetitionRepository competitionRepository;
	private final FriendRepository friendRepository;
	private final ConsumptionRepository consumptionRepository;
	private final FriendNotiService friendNotiService;
	private final UserRepository userRepository;

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<CompetitionResponse>> applyCompetition(
		CustomUserPrincipal customUserPrincipal, CompetitionRequest competitionRequest) {
		Users users = userRepository.getById(customUserPrincipal.getId());
		Friend friendWithUser = friendRepository.getById(competitionRequest.friendsId());
		friendWithUser.validateUser(users.getId());

		Friend friendWithUserFriend = friendRepository.findByUsersAndUserFriend(friendWithUser.getUserFriend(),
				friendWithUser.getUsers())
			.orElseThrow(() -> new CustomException(FriendErrorCode.FRIEND_NOT_FOUND));
		friendWithUserFriend.validateCompetitionStatus();

		Competition competition = Competition.of(
			competitionRequest.message(),
			competitionRequest.day(),
			competitionRequest.price(),
			competitionRequest.compensation(),
			friendWithUser.getUsers()
		);
		competitionRepository.save(competition);
		friendWithUser.addCompetition(competition);
		friendWithUserFriend.addCompetition(competition);

		CompetitionResponse competitionResponse = CompetitionResponse.from(friendWithUserFriend);

		String title = "[SUNNY] " + friendWithUser.getUsers().getNickname();
		String body = "님으로부터 대결 신청을 받았어요.";
		String bodyTitle = "대결 신청을 받았어요!";
		friendNotiService.sendNotifications(title, body, bodyTitle, friendWithUserFriend);
		return responseService.getSingleResponse(HttpStatus.OK.value(), competitionResponse,
			"대결 신청이 됐습니다.");
	}

	@Transactional
	public void acceptCompetition(CustomUserPrincipal customUserPrincipal, Long friendId) {
		Users users = userRepository.getById(customUserPrincipal.getId());
		Friend friendWithUser = friendRepository.getById(friendId);
		friendWithUser.validateUser(users.getId());

		Competition competition = competitionRepository.getById(friendWithUser.getCompetition().getId());
		competition.validateReceiveUser(friendWithUser.getUsers().getId());

		competition.updateStatus(CompetitionStatus.PROCEEDING);
		competition.addDate(LocalDate.now().plusDays(1),
			LocalDate.now().plusDays(1).plusDays(competition.getPeriod()));
		Friend friendWithUserFriend = friendRepository
			.findByUsersAndUserFriend(friendWithUser.getUserFriend(), friendWithUser.getUsers())
			.orElseThrow(() -> new CustomException(FriendErrorCode.FRIEND_NOT_FOUND));
		friendWithUserFriend.addCompetition(competition);

		//TODO title,body,bodyTitle 분리
		String title = "[SUNNY] " + friendWithUser.getUsers().getNickname();
		String body = "님이 대결을 수락했어요";
		String bodyTitle = "대결 신청에 대한 응답을 받았어요";
		friendNotiService.sendNotifications(title, body, bodyTitle, friendWithUserFriend);
	}

	@Transactional
	public void refuseFriend(CustomUserPrincipal customUserPrincipal, Long friendId) {
		Users users = userRepository.getById(customUserPrincipal.getId());
		Friend friendWithUser = friendRepository.getById(friendId);
		friendWithUser.validateUser(users.getId());

		Competition competition = competitionRepository.getById(friendWithUser.getCompetition().getId());
		competition.validateReceiveUser(friendWithUser.getUsers().getId());
		competition.updateStatus(CompetitionStatus.NONE);
		friendRepository.updateCompetitionToNull(competition.getId());

		Friend friendWithUserFriend = friendRepository
			.findByUsersAndUserFriend(friendWithUser.getUserFriend(), friendWithUser.getUsers())
			.orElseThrow(() -> new CustomException(FriendErrorCode.FRIEND_NOT_FOUND));
		String title = "[SUNNY] " + friendWithUser.getUsers().getNickname();
		String body = "님이 대결을 거절했어요";
		String bodyTitle = "대결 신청에 대한 응답을 받았어요";
		friendNotiService.sendNotifications(title, body, bodyTitle, friendWithUserFriend);
	}

	//TODO 대결 포기 배너 알림 필요 여부 논의 & 추가
	@Transactional
	public void giveUpCompetition(CustomUserPrincipal customUserPrincipal, Long friendId) {
		Users users = userRepository.getById(customUserPrincipal.getId());
		Friend friend = friendRepository.getById(friendId);
		friend.validateUser(users.getId());

		Competition competition = competitionRepository.getById(friend.getCompetition().getId());
		competition.updateStatus(CompetitionStatus.GIVE_UP);
		friendRepository.updateCompetitionToNull(competition.getId());
	}

	public ResponseEntity<CommonResponse.SingleResponse<CompetitionResponse>> getCompetition(
		CustomUserPrincipal customUserPrincipal, Long friendId) {
		Users users = userRepository.getById(customUserPrincipal.getId());
		Friend friend = friendRepository.getById(friendId);
		friend.validateUser(users.getId());
		friend.validateCompetition();

		return responseService.getSingleResponse(HttpStatus.OK.value(), CompetitionResponse.from(friend), "결과 조회");
	}

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<CompetitionStatusResponse>> getCompetitionStatus(
		CustomUserPrincipal customUserPrincipal, Long friendId) {
		Users users = userRepository.getById(customUserPrincipal.getId());
		Friend friendWithUser = friendRepository.getById(friendId);
		friendWithUser.validateUser(users.getId());
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