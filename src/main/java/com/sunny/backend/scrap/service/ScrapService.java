package com.sunny.backend.scrap.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
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

	public void addScrapToCommunity(CustomUserPrincipal customUserPrincipal,
		Long communityId) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		Community community = communityRepository.getById(communityId);

		user.validateScrapByCommunity(community.getId());

		Scrap scrap = Scrap.of(user, community);
		scrapRepository.save(scrap);
	}

	public void removeScrapFromCommunity(
		CustomUserPrincipal customUserPrincipal, Long communityId) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		Community community = communityRepository.getById(communityId);

		Scrap scrap = user.findScrapByCommunity(community.getId());

		scrapRepository.delete(scrap);
	}
}






