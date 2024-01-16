package com.sunny.backend.competition.service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.competition.dto.request.CompetitionRequest;
import com.sunny.backend.competition.dto.request.CompetitionRequestDto;
import com.sunny.backend.competition.dto.response.CompetitionResponseDto;
import com.sunny.backend.competition.domain.Competition;
import com.sunny.backend.competition.repository.CompetitionRepository;
import com.sunny.backend.consumption.repository.ConsumptionRepository;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.domain.Status;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.user.Users;
import com.sunny.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompetitionService {
	private final ResponseService responseService;
	private final CompetitionRepository competitionRepository;
	private final UserRepository userRepository;
	private final ConsumptionRepository consumptionRepository;

	public ResponseEntity<CommonResponse.GeneralResponse> applyCompetition(CustomUserPrincipal customUserPrincipal,
		CompetitionRequest competitionRequest) {
		Users userFriend = userRepository.getById(competitionRequest.friendsId());

		Competition competition = competitionRequest.of(userFriend, customUserPrincipal.getUsers());

		competitionRepository.save(competition);
		//  신청후 알람을 보내는 행위
		return responseService.getGeneralResponse(HttpStatus.OK.value(), "대결 신청이 됐습니다.");
	}

	@Transactional
	public void acceptCompetition(CustomUserPrincipal customUserPrincipal, Long competitionId) {
		Competition competition = competitionRepository.getById(competitionId);

		competition.validateCompetitionByUser(competition.getUsers().getId(), customUserPrincipal.getUsers().getId());
		competition.approveStatus();

		getByUserAndUserFriendAndCreateCompetition(competition.getUsers(), competition.getUserFriend(), competition);
	}

	@Transactional
	public void refuseFriend(CustomUserPrincipal customUserPrincipal, Long competitionId) {
		Competition competition = competitionRepository.getById(competitionId);

		competition.validateCompetitionByUser(competition.getUsers().getId(), customUserPrincipal.getUsers().getId());
		competitionRepository.deleteById(competitionId);
	}

	public void getByUserAndUserFriendAndCreateCompetition(Users users, Users userFriend, Competition competition) {
		Optional<Competition> optionalCompetition = competitionRepository
			.findByUsers_IdAndUserFriend_Id(userFriend.getId(), users.getId());

		if(optionalCompetition.isEmpty()) {
			Competition saveCompetition = Competition.builder()
				.message(competition.getMessage())
				.price(competition.getPrice())
				.compensation(competition.getCompensation())
				.startDate(competition.getStartDate())
				.endDate(competition.getEndDate())
				.users(userFriend)
				.userFriend(users)
				.status(Status.APPROVE)
				.build();
			competitionRepository.save(saveCompetition);
		} else {
			Competition getCompetition = optionalCompetition.get();
			getCompetition.validateStatus();
		}
	}

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<CompetitionResponseDto.CompetitionStatus>> getCompetitionStatus(
		CustomUserPrincipal customUserPrincipal, Long competitionId) {
		Competition competition = competitionRepository.findById(competitionId)
			.orElseThrow(() -> new IllegalArgumentException("Not Found Id" + competitionId));
		Users user = customUserPrincipal.getUsers();
		Users friends = userRepository.findById(competition.getUserFriend().getId())
			.orElseThrow(() -> new IllegalArgumentException("Not Found Id" + competition.getUserFriend().getId()));
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
			.competitionId(competitionId)
			.price(competition.getPrice())
			.compensation(competition.getCompensation())
			.endDate(competition.getEndDate())
			.dDay(diff.toMinutes() / 60)
			.username(user.getName())
			.friendName(friends.getName())
			.userPercent(userMoney / competition.getPrice())
			.friendsPercent(friendsMoney / competition.getPrice())
			.result(result)
			.build();
		return responseService.getSingleResponse(HttpStatus.OK.value(), competitionStatus, "결과 조회");
	}


}
