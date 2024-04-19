package com.sunny.backend.community.service;

import static com.sunny.backend.common.ComnConstant.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.common.photo.Photo;
import com.sunny.backend.common.photo.PhotoRepository;
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
	private final S3Util s3Util;
	private final RedisUtil redisUtil;
	private final CommentNotificationRepository commentNotificationRepository;
	private final UserRepository userRepository;

	@Transactional
	public CommunityResponse findCommunity(
		CustomUserPrincipal customUserPrincipal,
		Long communityId
	) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		Community community = communityRepository.getById(communityId);

		findByRedisAndSaveIfNotFound(user.getId(), community);

		return CommunityResponse.of(user, community);
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
	public Long createCommunity(
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

		return community.getId();
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
	public List<CommunityPageResponse> paginationNoOffsetBuilder(
		CustomUserPrincipal customUserPrincipal,
		Long communityId,
		SortType sortType,
		BoardType boardType,
		String searchText,
		Integer pageSize
	) {
		Users users = userRepository.getById(customUserPrincipal.getId());
		// Users communityUser = communityRepository.getById(communityId).getUsers();
		return communityRepository.paginationNoOffsetBuilder(users, communityId, sortType, boardType, searchText,
			pageSize);
	}

	@Transactional
	public void updateCommunity(
		CustomUserPrincipal customUserPrincipal,
		Long communityId,
		CommunityRequest communityRequest,
		List<MultipartFile> multipartFiles
	) {
		Users users = userRepository.getById(customUserPrincipal.getId());
		Community community = communityRepository.getById(communityId);
		community.validateByUserId(users.getId());
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
	}

	// TODO
	// 관계 정리되면 변경될 가능성 존재
	@Transactional
	public void deleteCommunity(
		CustomUserPrincipal customUserPrincipal, Long communityId) {
		Users users = userRepository.getById(customUserPrincipal.getId());
		Community community = communityRepository.getById(communityId);
		community.validateByUserId(users.getId());

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
	}

	@Transactional
	public ViewAndCommentResponse getCommentAndViewByCommunity(Long communityId) {
		Community community = communityRepository.getById(communityId);
		return ViewAndCommentResponse.from(community);
	}
}