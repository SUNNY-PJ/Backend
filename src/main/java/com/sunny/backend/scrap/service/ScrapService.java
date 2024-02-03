package com.sunny.backend.scrap.service;

import static com.sunny.backend.scrap.domain.QScrap.*;
import static com.sunny.backend.scrap.exception.ScrapErrorCode.*;

import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.community.repository.CommunityRepository;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.community.dto.response.CommunityResponse;
import com.sunny.backend.scrap.domain.Scrap;
import com.sunny.backend.scrap.repository.ScrapRepository;
import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.user.domain.Users;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScrapService {
	private final ScrapRepository scrapRepository;
	private final CommunityRepository communityRepository;
	private final ResponseService responseService;
	public ResponseEntity<CommonResponse.ListResponse<CommunityResponse>> getScrapsByUserId(
		CustomUserPrincipal customUserPrincipal) {
		Users user = customUserPrincipal.getUsers();
		List<Scrap> scrapList = scrapRepository.findAllByUsers_Id(user.getId());

		List<CommunityResponse> communityResponseList = scrapList.stream()
				.map(scrap -> CommunityResponse.of(scrap.getCommunity(), true))
				.toList();

		return responseService.getListResponse(HttpStatus.OK.value(), communityResponseList, "");
	}

	public ResponseEntity<CommonResponse.GeneralResponse> addScrapToCommunity(CustomUserPrincipal customUserPrincipal,
		Long communityId) {
		Users user = customUserPrincipal.getUsers();
		Community community = communityRepository.getById(communityId);

		scrapRepository.findByUsersAndCommunity(user, community)
			.ifPresent(e -> {
				throw new CustomException(SCRAP_ALREADY);
			});

		scrapRepository.save(Scrap.builder()
			.community(community)
			.users(user)
			.build());
		return responseService.getGeneralResponse(HttpStatus.OK.value(), "스크랩하였습니다.");
	}

	public ResponseEntity<CommonResponse.GeneralResponse> removeScrapFromCommunity(
			CustomUserPrincipal customUserPrincipal, Long communityId) {
		Users user = customUserPrincipal.getUsers();
		Community community = communityRepository.getById(communityId);

		Optional<Scrap> scrap = scrapRepository.findByUsersAndCommunity(user, community);

		if(scrap.isPresent()) {
			scrapRepository.delete(scrap.get());
			return responseService.getGeneralResponse(HttpStatus.OK.value(), "스크랩 게시글이 삭제 되었습니다.");
		} else {
			throw new CustomException(SCRAP_NOT_FOUND);
		}
	}
}






