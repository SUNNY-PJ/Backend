package com.sunny.backend.scrap.service;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.community.repository.CommunityRepository;
import com.sunny.backend.scrap.domain.Scrap;
import com.sunny.backend.scrap.repository.ScrapRepository;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ScrapService {
	private final UserRepository userRepository;
	private final ScrapRepository scrapRepository;
	private final CommunityRepository communityRepository;
	private final ResponseService responseService;

	public ResponseEntity<CommonResponse.GeneralResponse> addScrapToCommunity(CustomUserPrincipal customUserPrincipal,
		Long communityId) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		Community community = communityRepository.getById(communityId);

		user.validateScrapByCommunity(community.getId());

		Scrap scrap = Scrap.of(user, community);
		scrapRepository.save(scrap);
		return responseService.getGeneralResponse(HttpStatus.OK.value(), "스크랩하였습니다.");
	}

	public ResponseEntity<CommonResponse.GeneralResponse> removeScrapFromCommunity(
		CustomUserPrincipal customUserPrincipal, Long communityId) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		Scrap scrap = user.findScrapByCommunity(communityId);
		scrapRepository.delete(scrap);
		return responseService.getGeneralResponse(HttpStatus.OK.value(), "스크랩 게시글이 삭제 되었습니다.");
	}
}






