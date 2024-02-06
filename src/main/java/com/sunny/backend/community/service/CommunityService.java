package com.sunny.backend.community.service;

import com.sunny.backend.comment.repository.CommentRepository;
import com.sunny.backend.common.photo.Photo;
import com.sunny.backend.community.domain.BoardType;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.community.domain.SortType;
import com.sunny.backend.community.dto.response.CommunityResponse.ViewAndCommentResponse;
import com.sunny.backend.notification.domain.CommentNotification;
import com.sunny.backend.notification.repository.CommentNotificationRepository;
import com.sunny.backend.scrap.domain.Scrap;
import com.sunny.backend.scrap.repository.ScrapRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import javax.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.community.dto.request.CommunityRequest;
import com.sunny.backend.community.dto.response.CommunityResponse;
import com.sunny.backend.community.repository.CommunityRepository;
import com.sunny.backend.common.photo.PhotoRepository;
import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.util.S3Util;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.util.RedisUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityService {
	private final CommunityRepository communityRepository;
	private final PhotoRepository photoRepository;
	private final ScrapRepository scrapRepository;
	private final ResponseService responseService;
	private final S3Util s3Util;
	private final RedisUtil redisUtil;
	private final CommentNotificationRepository commentNotificationRepository;
	private final CommentRepository commentRepository;

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<CommunityResponse>> findCommunity(
			CustomUserPrincipal customUserPrincipal, Long communityId) {
		Users user = customUserPrincipal.getUsers();
		Community community = communityRepository.getById(communityId);
		String viewCount = redisUtil.getData(String.valueOf(user.getId()));

		if (StringUtils.isBlank(viewCount)) {
			redisUtil.setValuesWithTimeout(String.valueOf(user.getId()), communityId + "_",
					calculateTimeUntilMidnight());
			community.increaseView();
		} else {
			List<String> redisBoardList = Arrays.asList(viewCount.split("_"));
			boolean isViewed = redisBoardList.contains(String.valueOf(communityId));

			if (!isViewed) {
				viewCount += communityId + "_";
				redisUtil.setValuesWithTimeout(String.valueOf(user.getId()), viewCount,
						calculateTimeUntilMidnight());
				community.updateView();
			}
		}

		boolean isScrap = false;
		Optional<Scrap> scrap = scrapRepository.findByUsersAndCommunity(user, community);
		if(scrap.isPresent()) {
			isScrap = true;
		}

		CommunityResponse communityResponse = CommunityResponse.of(community, isScrap);
		return responseService.getSingleResponse(
				HttpStatus.OK.value(), communityResponse, "게시글을 성공적으로 불러왔습니다.");
	}

	public static long calculateTimeUntilMidnight() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime midnight = now.truncatedTo(ChronoUnit.DAYS).plusDays(1);
		return ChronoUnit.SECONDS.between(now, midnight);
	}

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<CommunityResponse>> createCommunity(
			CustomUserPrincipal customUserPrincipal,
			CommunityRequest communityRequest, List<MultipartFile> multipartFileList) {
		Users user = customUserPrincipal.getUsers();
		Community community = Community.builder()
				.title(communityRequest.getTitle())
				.contents(communityRequest.getContents())
				.boardType(communityRequest.getType())
				.createdAt(LocalDateTime.now())
				.users(user)
				.build();
		if (multipartFileList != null && !multipartFileList.isEmpty()) {
			List<Photo> photoList = new ArrayList<>();
			for (MultipartFile multipartFile : multipartFileList) {
				Photo photo = Photo.builder()
						.filename(multipartFile.getOriginalFilename())
						.fileSize(multipartFile.getSize())
						.fileUrl(s3Util.upload(multipartFile))
						.community(community)
						.build();
				photoList.add(photo);
			}
			photoRepository.saveAll(photoList);
			community.addPhoto(photoList);
		}
		communityRepository.save(community);
		community.updateModifiedAt(community.getCreatedAt());
		if (user.getCommunityList() == null) {
			user.addCommunity(community);
		}
		CommunityResponse communityResponse = CommunityResponse.of(community,  false);
		return responseService.getSingleResponse(HttpStatus.OK.value(), communityResponse,
				"게시글을 성공적으로 작성했습니다.");
	}
	@Transactional(readOnly = true)
	public ResponseEntity<CommonResponse.SingleResponse<List<CommunityResponse.PageResponse>>> paginationNoOffsetBuilder(
			CustomUserPrincipal customUserPrincipal,Long communityId,
			SortType sortType, BoardType boardType, String searchText, int pageSize) {
		Users users=customUserPrincipal.getUsers();
		List<CommunityResponse.PageResponse> result = communityRepository.paginationNoOffsetBuilder(
				users,communityId, sortType, boardType, searchText, pageSize);
		return responseService.getSingleResponse(HttpStatus.OK.value(), result,
				"게시판을 성공적으로 조회했습니다.");
	}

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<CommunityResponse>> updateCommunity(
			CustomUserPrincipal customUserPrincipal, Long communityId,
			CommunityRequest communityRequest, List<MultipartFile> files) {
		Users user = customUserPrincipal.getUsers();
		Community community = communityRepository.getById(communityId);
		Community.validateCommunityByUser(community.getUsers().getId(), user.getId());
		community.getPhotoList().clear();
		community.updateCommunity(communityRequest);
		community.updateModifiedAt(LocalDateTime.now());
		if (files != null && !files.isEmpty()) {
			List<Photo> existingPhotos = photoRepository.findByCommunityId(communityId);
			photoRepository.deleteAll(existingPhotos);
			for (Photo photo : existingPhotos) {
				s3Util.deleteFile(photo.getFileUrl());
			}
			List<Photo> photoList = new ArrayList<>();
			for (MultipartFile multipartFile : files) {
				Photo photo = Photo.builder()
						.filename(multipartFile.getOriginalFilename())
						.fileSize(multipartFile.getSize())
						.fileUrl(s3Util.upload(multipartFile))
						.community(community)
						.build();
				photoList.add(photo);
			}
			photoRepository.saveAll(photoList);
			community.addPhoto(photoList);
		}

		boolean isScrap = false;
		Optional<Scrap> scrap = scrapRepository.findByUsersAndCommunity(user, community);
		if(scrap.isPresent()) {
			isScrap = true;
		}

		CommunityResponse communityResponse = CommunityResponse.of(community, isScrap);
		return responseService.getSingleResponse(HttpStatus.OK.value(), communityResponse,
				"게시글 수정을 완료했습니다.");
	}
	@Transactional
	public ResponseEntity<CommonResponse.GeneralResponse> deleteCommunity(
			CustomUserPrincipal customUserPrincipal, Long communityId) {
		Users user = customUserPrincipal.getUsers();
		Community community = communityRepository.getById(communityId);

		Community.validateCommunityByUser(community.getUsers().getId(), user.getId());

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
	public ResponseEntity<CommonResponse.SingleResponse<CommunityResponse.ViewAndCommentResponse>> getCommentAndViewByCommunity(
			CustomUserPrincipal customUserPrincipal, Long communityId) {
		Community community = communityRepository.getById(communityId);
		ViewAndCommentResponse viewAndCommentResponse = CommunityResponse.ViewAndCommentResponse.from(community);
		return responseService.getSingleResponse(HttpStatus.OK.value(),viewAndCommentResponse,
				"게시글 조회수와 댓글수를 불러왔습니다.");
	}
}