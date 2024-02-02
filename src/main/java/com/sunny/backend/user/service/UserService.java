package com.sunny.backend.user.service;

import static com.sunny.backend.common.ComnConstant.*;
import static com.sunny.backend.community.domain.QCommunity.*;
import static com.sunny.backend.declaration.domain.DeclarationStatus.*;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.comment.domain.Comment;
import com.sunny.backend.comment.repository.CommentRepository;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.community.repository.CommunityRepository;
import com.sunny.backend.declaration.domain.CommentDeclaration;
import com.sunny.backend.declaration.domain.CommunityDeclaration;
import com.sunny.backend.declaration.dto.DeclareRequest;
import com.sunny.backend.declaration.repository.CommentDeclarationRepository;
import com.sunny.backend.declaration.repository.CommunityDeclarationRepository;
import com.sunny.backend.user.dto.ProfileResponse;
import com.sunny.backend.scrap.repository.ScrapRepository;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.dto.UserCommentResponse;
import com.sunny.backend.user.dto.UserCommunityResponse;
import com.sunny.backend.user.dto.UserScrapResponse;
import com.sunny.backend.user.repository.UserRepository;
import com.sunny.backend.util.S3Util;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
	private final UserRepository userRepository;
	private final CommunityRepository communityRepository;
	private final CommentRepository commentRepository;
	private final ResponseService responseService;
	private final ScrapRepository scrapRepository;
	private final CommunityDeclarationRepository communityDeclarationRepository;
	private final CommentDeclarationRepository commentDeclarationRepository;
	private final S3Util s3Util;

	public Users checkUserId(CustomUserPrincipal customUserPrincipal, Long userId) {
		if (userId != null) {
			return userRepository.getById(userId);
		}
		return customUserPrincipal.getUsers();
	}

	public ProfileResponse getUserProfile(CustomUserPrincipal customUserPrincipal, Long userId) {
		Users user = checkUserId(customUserPrincipal, userId);
		return ProfileResponse.from(user);
	}

	public List<UserCommunityResponse> getUserCommunityList(CustomUserPrincipal customUserPrincipal, Long userId) {
		Users user = checkUserId(customUserPrincipal, userId);

		return communityRepository.findAllByUsers_Id(user.getId())
			.stream()
			.map(UserCommunityResponse::from)
			.toList();
	}

	@Transactional(readOnly = true)
	public List<UserCommentResponse> getCommentByUserId(CustomUserPrincipal customUserPrincipal, Long userId) {
		Users user = checkUserId(customUserPrincipal, userId);

		return commentRepository.findAllByUsers_Id(user.getId())
			.stream()
			.map(UserCommentResponse::from)
			.toList();
	}

	public List<UserScrapResponse> getScrapList(CustomUserPrincipal customUserPrincipal) {
		return scrapRepository.findAllByUsers_Id(customUserPrincipal.getUsers().getId())
			.stream()
			.map(scrap -> UserScrapResponse.from(scrap.getCommunity()))
			.toList();
	}

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<ProfileResponse>> updateProfile(
		CustomUserPrincipal customUserPrincipal, MultipartFile profile) {

		Users user = customUserPrincipal.getUsers();
		// 새 프로필 업로드
		if (profile != null && !profile.isEmpty()) {
			String uploadedProfileUrl = s3Util.upload(profile);
			user.updateProfile(uploadedProfileUrl);
		} else if (profile == null) {
			user.updateProfile(SUNNY_DEFAULT_IMAGE);
		}
		userRepository.save(user);
		ProfileResponse profileResponse = ProfileResponse.from(user);
		return responseService.getSingleResponse(HttpStatus.OK.value(), profileResponse, "프로필 변경 완료");
	}

	public ResponseEntity<CommonResponse.GeneralResponse> declareCommunity(CustomUserPrincipal customUserPrincipal,
		DeclareRequest declareRequest) {
		switch (declareRequest.status()) {
			case COMMUNITY -> {
				Community community = communityRepository.getById(declareRequest.id());
				CommunityDeclaration communityDeclaration = CommunityDeclaration.builder()
					.users(customUserPrincipal.getUsers())
					.community(community)
					.reason(declareRequest.reason())
					.build();
				communityDeclarationRepository.save(communityDeclaration);
				return responseService.getGeneralResponse(HttpStatus.OK.value(), "게시글을 신고했습니다.");
			}
			case COMMENT -> {
				Comment comment = commentRepository.getById(declareRequest.id());
				CommentDeclaration commentDeclaration = CommentDeclaration.builder()
					.users(customUserPrincipal.getUsers())
					.comment(comment)
					.reason(declareRequest.reason())
					.build();
				commentDeclarationRepository.save(commentDeclaration);
				return responseService.getGeneralResponse(HttpStatus.OK.value(), "게시글을 신고했습니다.");
			}
		}
		return null;
	}
}
