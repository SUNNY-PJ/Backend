package com.sunny.backend.scrap.service;

import static com.sunny.backend.common.CommonErrorCode.ALREADY_SCRAP;
import static com.sunny.backend.common.CommonErrorCode.NOT_FOUND_SCRAP;


import com.sunny.backend.common.CommonCustomException;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.community.repository.CommunityRepository;
import java.util.List;

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

		Scrap scrap = scrapRepository.findByUsersAndCommunity(user, community);
		if (scrap == null) {
			scrapRepository.save(Scrap.builder()
					.community(community)
					.users(user)
					.build());
			return responseService.getGeneralResponse(HttpStatus.OK.value(), "스크랩하였습니다.");
		} else {
			throw new CommonCustomException(ALREADY_SCRAP);
		}
	}

	public ResponseEntity<CommonResponse.GeneralResponse> removeScrapFromCommunity(
			CustomUserPrincipal customUserPrincipal, Long communityId) {
		Users user = customUserPrincipal.getUsers();
		Community community = communityRepository.getById(communityId);

		Scrap deleteScrap = scrapRepository.findByUsersAndCommunity(user, community);
		if (deleteScrap != null) {
			scrapRepository.delete(deleteScrap);
			return responseService.getGeneralResponse(HttpStatus.OK.value(), "스크랩 게시글이 삭제 되었습니다.");
		} else {
			throw new CommonCustomException(NOT_FOUND_SCRAP);
		}
	}
}






