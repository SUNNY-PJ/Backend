package com.sunny.backend.competition.service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.CustomException;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.competition.dto.request.CompetitionRequest;
import com.sunny.backend.competition.dto.request.CompetitionRequestDto;
import com.sunny.backend.competition.dto.response.CompetitionApplyResponse;
import com.sunny.backend.competition.dto.response.CompetitionResponseDto;
import com.sunny.backend.competition.domain.Competition;
import com.sunny.backend.competition.repository.CompetitionRepository;
import com.sunny.backend.consumption.repository.ConsumptionRepository;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.domain.Status;
import com.sunny.backend.friends.exception.FriendErrorCode;
import com.sunny.backend.friends.repository.FriendRepository;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.user.Users;
import com.sunny.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompetitionService {
	private final ResponseService responseService;
	private final CompetitionRepository competitionRepository;
	private final FriendRepository friendRepository;
	private final ConsumptionRepository consumptionRepository;

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<CompetitionApplyResponse>> applyCompetition(CustomUserPrincipal customUserPrincipal,
		CompetitionRequest competitionRequest) {
		Friend friendWithUser = friendRepository.getById(competitionRequest.friendsId());
		friendWithUser.validateFriendsByUser(friendWithUser.getUsers().getId(), customUserPrincipal.getUsers().getId());

		Friend friendWithUserFriend = friendRepository
			.findByUsers_IdAndUserFriend_Id(friendWithUser.getUserFriend().getId(), friendWithUser.getUsers().getId())
			.orElseThrow(() -> new CustomException(FriendErrorCode.FRIEND_NOT_FOUND));

		Competition competition = competitionRequest.toEntity();
		competitionRepository.save(competition);
		friendWithUserFriend.addCompetition(competition);

		CompetitionApplyResponse competitionApplyResponse =  CompetitionApplyResponse.of(friendWithUserFriend.getId(),
			friendWithUserFriend.getUsers().getName(), competition);
		//  신청후 알람을 보내는 행위
		return responseService.getSingleResponse(HttpStatus.OK.value(), competitionApplyResponse, "대결 신청이 됐습니다.");
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


		Duration diff = Duration.between(LocalDate.now(), competition.getEndDate());

		// 날짜 간의 소비 금액 구하기 로직
		Long userMoney = consumptionRepository.getComsumptionMoney(user.getId(), competition.getStartDate(),
			competition.getEndDate());
		Long friendsMoney = consumptionRepository.getComsumptionMoney(user.getId(), competition.getStartDate(),
			competition.getEndDate());

		String result;
		if (userMoney > friendsMoney) {
			result = "유저가 이기고 있습니다.";
		} else if (userMoney < friendsMoney) {
			result = "유저가 지고 있습니다.";
		} else {
			result = "비기고 있습니다.";
		}

		CompetitionResponseDto.CompetitionStatus competitionStatus = CompetitionResponseDto.CompetitionStatus.builder()
			.competitionId(competition.getId())
			.price(competition.getPrice())
			.compensation(competition.getCompensation())
			.endDate(competition.getEndDate())
			.dDay(diff.toMinutes() / 60)
			.username(user.getName())
			.friendName(userFriend.getName())
			.userPercent(userMoney / competition.getPrice())
			.friendsPercent(friendsMoney / competition.getPrice())
			.result(result)
			.build();
		return responseService.getSingleResponse(HttpStatus.OK.value(), competitionStatus, "결과 조회");
	}

}
