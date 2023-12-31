package com.sunny.backend.service;

import java.time.Duration;
import java.time.LocalDate;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.dto.request.CompetitionRequestDto;
import com.sunny.backend.dto.response.CompetitionResponseDto;
import com.sunny.backend.entity.Competition;
import com.sunny.backend.repository.CompetitionRepository;
import com.sunny.backend.repository.consumption.ConsumptionRepository;
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
		CompetitionRequestDto.CompetitionApply competitionApply) {
		Users friends = userRepository.findById(competitionApply.getFriendsId())
			.orElseThrow(() -> new IllegalArgumentException("Not Found Id" + competitionApply.getFriendsId()));
		Competition competition = Competition.builder()
			.message(competitionApply.getMessage())
			.price(competitionApply.getPrice())
			.compensation(competitionApply.getCompensation())
			.startDate(competitionApply.getStartDate())
			.endDate(competitionApply.getEndDate())
			.users(customUserPrincipal.getUsers())
			.friends(friends)
			.build();
		competitionRepository.save(competition);
		//  신청후 알람을 보내는 행위
		return responseService.getGeneralResponse(HttpStatus.OK.value(), "대결 신청이 됐습니다.");
	}

	@Transactional
	public ResponseEntity<CommonResponse.GeneralResponse> acceptCompetition(CustomUserPrincipal customUserPrincipal,
		CompetitionRequestDto.CompetitionAccept competitionAccept) {
		Competition competition = competitionRepository.findById(competitionAccept.getCompetitionId())
			.orElseThrow(() -> new IllegalArgumentException("Not Found Id" + competitionAccept.getCompetitionId()));

		if (competition.getFriends().getId().equals(customUserPrincipal.getUsers().getId())) {
			competition.setApprove(competitionAccept.getApprove());
			if (competition.getApprove().equals('Y')) {
				return responseService.getGeneralResponse(HttpStatus.OK.value(),
					customUserPrincipal.getUsers().getName() + "님이 대결 신청을 승낙했어요 :)");
			} else {
				return responseService.getGeneralResponse(HttpStatus.OK.value(),
					customUserPrincipal.getUsers().getName() + "님이 대결 신청을 거절했어요 :(");
			}

			// 위 로직 제거하고 알람만?
		}
		return responseService.getGeneralResponse(HttpStatus.OK.value(), "잘못된 사용자입니다.");
	}

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<CompetitionResponseDto.CompetitionStatus>> getCompetitionStatus(
		CustomUserPrincipal customUserPrincipal, Long competitionId) {
		Competition competition = competitionRepository.findById(competitionId)
			.orElseThrow(() -> new IllegalArgumentException("Not Found Id" + competitionId));
		Users user = customUserPrincipal.getUsers();
		Users friends = userRepository.findById(competition.getFriends().getId())
			.orElseThrow(() -> new IllegalArgumentException("Not Found Id" + competition.getFriends().getId()));
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
