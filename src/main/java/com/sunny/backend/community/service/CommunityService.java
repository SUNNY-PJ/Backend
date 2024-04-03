package com.sunny.backend.community.service;

import static com.sunny.backend.common.ComnConstant.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.photo.Photo;
import com.sunny.backend.common.photo.PhotoRepository;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.community.domain.BoardType;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.community.domain.SortType;
import com.sunny.backend.community.dto.request.CommunityRequest;
import com.sunny.backend.community.dto.response.CommunityPageResponse;
import com.sunny.backend.community.dto.response.CommunityResponse;
import com.sunny.backend.community.dto.response.ViewAndCommentResponse;
import com.sunny.backend.community.repository.CommunityRepository;
import com.sunny.backend.notification.domain.CommentNotification;
import com.sunny.backend.notification.repository.CommentNotificationRepository;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.repository.UserRepository;
import com.sunny.backend.util.RedisUtil;
import com.sunny.backend.util.S3Util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityService {
	private final CommunityRepository communityRepository;
	private final PhotoRepository photoRepository;
	private final ResponseService responseService;
	private final S3Util s3Util;
	private final RedisUtil redisUtil;
	private final CommentNotificationRepository commentNotificationRepository;
	private final UserRepository userRepository;

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<CommunityResponse>> findCommunity(
		CustomUserPrincipal customUserPrincipal, Long communityId) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		Community community = communityRepository.getById(communityId);

		findByRedisAndSaveIfNotFound(user.getId(), community);

		return responseService.getSingleResponse(HttpStatus.OK.value(), CommunityResponse.from(community),
			"게시글을 성공적으로 불러왔습니다.");
	}

	public void findByRedisAndSaveIfNotFound(Long userId, Community community) {
		String redisUserKey = String.valueOf(userId);
		String redisValues = redisUtil.getData(redisUserKey);
		if (StringUtils.isBlank(redisValues)) {
			saveRedisAndCommunityIncreaseView(redisUserKey, redisValues, community);
		} else {
			boolean isViewed = Arrays.stream(redisValues.split(REDIS_SEPARATOR))
				.anyMatch(id -> id.equals(String.valueOf(community.getId())));
			if (!isViewed) {
				saveRedisAndCommunityIncreaseView(redisUserKey, redisValues, community);
			}
		}
	}

	public void saveRedisAndCommunityIncreaseView(String redisKey, String redisValues, Community community) {
		redisValues += community.getId() + REDIS_SEPARATOR;
		redisUtil.setValuesWithTimeout(redisKey, redisValues, calculateTimeUntilMidnight());
		community.increaseView();
	}

	public long calculateTimeUntilMidnight() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime midnight = now.truncatedTo(ChronoUnit.DAYS).plusDays(1);
		return ChronoUnit.SECONDS.between(now, midnight);
	}

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<CommunityResponse>> createCommunity(
		CustomUserPrincipal customUserPrincipal,
		CommunityRequest communityRequest,
		List<MultipartFile> multipartFiles
	) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		Community community = Community.of(
			communityRequest.getTitle(),
			communityRequest.getContents(),
			communityRequest.getType(),
			user
		);

		if (multipartFiles != null && !multipartFiles.isEmpty()) {
			savePhotoFromMultipartFile(multipartFiles, community);
		}

		communityRepository.save(community);
		user.addCommunity(community);

		return responseService.getSingleResponse(HttpStatus.OK.value(), CommunityResponse.from(community),
			"게시글을 성공적으로 작성했습니다.");
	}

	public void savePhotoFromMultipartFile(List<MultipartFile> multipartFileList, Community community) {
		List<Photo> photos = multipartFileList.stream()
			.map(multipartFile -> Photo.of(
					community,
					multipartFile.getOriginalFilename(),
					s3Util.upload(multipartFile),
					multipartFile.getSize()
				)
			)
			.toList();
		photoRepository.saveAll(photos);
		community.addPhotos(photos);
	}

	@Transactional(readOnly = true)
	public ResponseEntity<CommonResponse.SingleResponse<List<CommunityPageResponse>>> paginationNoOffsetBuilder(
		CustomUserPrincipal customUserPrincipal, Long communityId,
		SortType sortType, BoardType boardType, String searchText, Integer pageSize) {
		Users users = customUserPrincipal.getUsers();
		List<CommunityPageResponse> result = communityRepository.paginationNoOffsetBuilder(
			users, communityId, sortType, boardType, searchText, pageSize);
		return responseService.getSingleResponse(HttpStatus.OK.value(), result,
			"게시판을 성공적으로 조회했습니다.");
	}

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<CommunityResponse>> updateCommunity(
		CustomUserPrincipal customUserPrincipal, Long communityId,
		CommunityRequest communityRequest, List<MultipartFile> multipartFiles) {
		Users user = customUserPrincipal.getUsers();
		Community community = communityRepository.getById(communityId);
		community.validateByUserId(user.getId());
		community.updateCommunity(communityRequest);

		if (multipartFiles != null && !multipartFiles.isEmpty()) {
			community.clearPhoto();
			List<Photo> existingPhotos = photoRepository.findByCommunityId(communityId);
			photoRepository.deleteAll(existingPhotos);
			for (Photo photo : existingPhotos) {
				s3Util.deleteFile(photo.getFileUrl());
			}
			savePhotoFromMultipartFile(multipartFiles, community);
		}

		return responseService.getSingleResponse(HttpStatus.OK.value(), CommunityResponse.from(community),
			"게시글 수정을 완료했습니다.");
	}

	// TODO
	// 관계 정리되면 변경될 가능성 존재
	@Transactional
	public ResponseEntity<CommonResponse.GeneralResponse> deleteCommunity(
		CustomUserPrincipal customUserPrincipal, Long communityId) {
		Users user = customUserPrincipal.getUsers();
		Community community = communityRepository.getById(communityId);
		community.validateByUserId(user.getId());

		List<CommentNotification> commentNotifications = commentNotificationRepository.findByCommunityId(communityId);
		for (CommentNotification commentNotification : commentNotifications) {
			commentNotificationRepository.deleteById(commentNotification.getId());
		}
		List<Photo> photoList = photoRepository.findByCommunityId(communityId);
		for (Photo existingFile : photoList) {
			s3Util.deleteFile(existingFile.getFileUrl());
		}
		photoRepository.deleteByCommunityId(communityId);
		communityRepository.deleteById(communityId);
		return responseService.getGeneralResponse(HttpStatus.OK.value(), "게시글을 삭제했습니다.");
	}

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<ViewAndCommentResponse>> getCommentAndViewByCommunity(
		CustomUserPrincipal customUserPrincipal, Long communityId) {
		Community community = communityRepository.getById(communityId);
		return responseService.getSingleResponse(HttpStatus.OK.value(), ViewAndCommentResponse.from(community),
			"게시글 조회수와 댓글수를 불러왔습니다.");
	}
}