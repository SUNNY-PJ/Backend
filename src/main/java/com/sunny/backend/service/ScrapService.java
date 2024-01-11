package com.sunny.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.dto.response.community.CommunityResponse;
import com.sunny.backend.entity.Community;
import com.sunny.backend.entity.Scrap;
import com.sunny.backend.repository.ScrapRepository;
import com.sunny.backend.repository.community.CommunityRepository;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.user.Users;
import com.sunny.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScrapService {
	private final ScrapRepository scrapRepository;
	private final CommunityRepository communityRepository;
	private final ResponseService responseService;

	//스크랩 조회
	public ResponseEntity<CommonResponse.ListResponse<CommunityResponse>> getScrapsByUserId(
		CustomUserPrincipal customUserPrincipal) {
		Users user = customUserPrincipal.getUsers();
		List<Scrap> scrapList = scrapRepository.findAllByUsers_Id(user.getId()); //user id 이용해서 전체 스크랩 조회

		List<CommunityResponse> communityResponseList = scrapList.stream()
				.map(scrap -> CommunityResponse.of(scrap.getCommunity(), false))
			.collect(Collectors.toList());

		return responseService.getListResponse(HttpStatus.OK.value(), communityResponseList, "");
	}

	//스크랩 추가
	public ResponseEntity<CommonResponse.GeneralResponse> addScrapToCommunity(CustomUserPrincipal customUserPrincipal,
		Long communityId) {
		Users user = customUserPrincipal.getUsers();

		Community community = communityRepository.findById(communityId)
			.orElseThrow(() -> new NotFoundException("could not found community"));
		Scrap scrap = Scrap.builder()
			.community(community)
			.users(user)
			.build();
		scrapRepository.save(scrap);

		return responseService.getGeneralResponse(HttpStatus.OK.value(), "스크랩하였습니다.");
	}

	//스크랩 취소
	public ResponseEntity<CommonResponse.GeneralResponse> removeScrapFromCommunity(
		CustomUserPrincipal customUserPrincipal,
		Long communityId) {

		try {
			Users user = customUserPrincipal.getUsers();
			Community community = communityRepository.findById(communityId)
				.orElseThrow(() -> new NotFoundException("could not found community"));

			Scrap deleteScrap = scrapRepository.findByUsersAndCommunity(user, community);
			if (deleteScrap != null) {
				scrapRepository.delete(deleteScrap);
				return responseService.getGeneralResponse(HttpStatus.OK.value(), "스크랩 게시글이 삭제 되었습니다.");
			} else {
				return responseService.getGeneralResponse(HttpStatus.NOT_FOUND.value(), "스크랩 게시글을 찾을 수 없습니다.");
			}

		} catch (Exception e) {
			return responseService.getGeneralResponse(HttpStatus.BAD_REQUEST.value(), "잘못된 요청입니다.");
		}
	}
}






