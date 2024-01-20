package com.sunny.backend.user.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.comment.repository.CommentRepository;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.community.repository.CommunityRepository;
import com.sunny.backend.user.dto.ProfileResponse;
import com.sunny.backend.comment.dto.response.CommentResponse;
import com.sunny.backend.community.dto.response.CommunityResponse;
import com.sunny.backend.scrap.repository.ScrapRepository;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.scrap.dto.response.ScrapResponse;
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

	public List<CommunityResponse.PageResponse> getUserCommunityList(CustomUserPrincipal customUserPrincipal,
		Long userId) {
		Users user = checkUserId(customUserPrincipal, userId);

		return communityRepository.findAllByUsers_Id(user.getId())
			.stream()
			.map(CommunityResponse.PageResponse::from)
			.toList();
	}

	@Transactional(readOnly = true)
	public List<CommentResponse.MyComment> getCommentByUserId(CustomUserPrincipal customUserPrincipal, Long userId) {
		Users user = checkUserId(customUserPrincipal, userId);

		return commentRepository.findAllByUsers_Id(user.getId())
			.stream()
			.map(CommentResponse.MyComment::from)
			.toList();
	}

	public List<ScrapResponse> getScrapList(CustomUserPrincipal customUserPrincipal) {
		Users user = customUserPrincipal.getUsers();
		return scrapRepository.findAllByUsers_Id(user.getId())
			.stream()
			.map(scrap -> ScrapResponse.from(scrap.getCommunity()))
			.toList();
	}

	public ResponseEntity<CommonResponse.SingleResponse<ProfileResponse>> updateProfile(
		CustomUserPrincipal customUserPrincipal, MultipartFile profile) {

		Users user = customUserPrincipal.getUsers();
		// 새 프로필 업로드
		if (profile != null && !profile.isEmpty()) {
			String uploadedProfileUrl = s3Util.upload(profile);
			user.setProfile(uploadedProfileUrl);
		} else if (profile == null) {
			user.setProfile("https://sunny-pj.s3.ap-northeast-2.amazonaws.com/Profile+Image.png");
		}
		ProfileResponse profileResponse = ProfileResponse.from(user);
		userRepository.save(user);

		return responseService.getSingleResponse(HttpStatus.OK.value(), profileResponse, "프로필 변경 완료");
	}

	public ResponseEntity<CommonResponse.GeneralResponse> deleteAccount(
		CustomUserPrincipal customUserPrincipal) {
		Users user = customUserPrincipal.getUsers();
		userRepository.deleteById(user.getId());
		return responseService.getGeneralResponse(HttpStatus.OK.value(), "성공적으로 탈퇴 되었습니다.");
	}
}
