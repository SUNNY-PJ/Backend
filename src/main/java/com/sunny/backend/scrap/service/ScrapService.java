package com.sunny.backend.scrap.service;

import static com.sunny.backend.scrap.exception.ScrapErrorCode.*;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.community.repository.CommunityRepository;
import com.sunny.backend.scrap.domain.Scrap;
import com.sunny.backend.scrap.repository.ScrapRepository;
import com.sunny.backend.user.domain.Users;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScrapService {
	private final ScrapRepository scrapRepository;
	private final CommunityRepository communityRepository;
	private final ResponseService responseService;

	@Transactional
	public ResponseEntity<CommonResponse.GeneralResponse> addScrapToCommunity(CustomUserPrincipal customUserPrincipal,
		Long communityId) {
		Users user = customUserPrincipal.getUsers();
		Community community = communityRepository.getById(communityId);

		Optional<Scrap> scrapOptional = scrapRepository.findByUsersAndCommunity(user, community);
		if (scrapOptional.isPresent()) {
			throw new CustomException(SCRAP_ALREADY);
		}

		Scrap scrap = Scrap.builder()
			.community(community)
			.users(user)
			.build();
		scrapRepository.save(scrap);
		user.addScrap(scrap);
		return responseService.getGeneralResponse(HttpStatus.OK.value(), "스크랩하였습니다.");
	}

	public ResponseEntity<CommonResponse.GeneralResponse> removeScrapFromCommunity(
		CustomUserPrincipal customUserPrincipal, Long communityId) {
		Users user = customUserPrincipal.getUsers();
		Community community = communityRepository.getById(communityId);

		Optional<Scrap> scrap = scrapRepository.findByUsersAndCommunity(user, community);
		if (scrap.isEmpty()) {
			throw new CustomException(SCRAP_NOT_FOUND);
		}

		scrapRepository.delete(scrap.get());
		return responseService.getGeneralResponse(HttpStatus.OK.value(), "스크랩 게시글이 삭제 되었습니다.");
	}
}






