package com.sunny.backend.community.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.comment.domain.Comment;
import com.sunny.backend.comment.repository.CommentRepository;
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
import com.sunny.backend.report.domain.CommentReport;
import com.sunny.backend.report.domain.CommunityReport;
import com.sunny.backend.report.repository.CommentReportRepository;
import com.sunny.backend.report.repository.CommunityReportRepository;
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
	private final CommentReportRepository commentReportRepository;
	private final CommunityReportRepository communityReportRepository;
	private final CommentRepository commentRepository;

	@Transactional
	public CommunityResponse findCommunity(
		CustomUserPrincipal customUserPrincipal,
		Long communityId
	) {
		Users user = userRepository.getById(customUserPrincipal.getId());
		Community community = communityRepository.getById(communityId);

		//조회수 중복 방지를 위해 check
		redisUtil.incrementCommunityViewIfNotViewed(user.getId(), community.getId());

		return CommunityResponse.of(user, community);
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
		//multipartFiles 객체 자체가 null이거나 multipartFiles 객체는 넘겨받았으나 빈 리스트일 경우
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

	// TODO 관계 정리되면 변경될 가능성 존재
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

		List<Comment> comments = commentRepository.findAllByCommunity_Id(communityId);
		for (Comment comment : comments) {
			List<CommentReport> commentReports = commentReportRepository.findByComment_Id(comment.getId());
			for (CommentReport commentReport : commentReports) {
				commentReportRepository.deleteById(commentReport.getId());
			}
		}

		List<Photo> photoList = photoRepository.findByCommunityId(communityId);
		for (Photo existingFile : photoList) {
			s3Util.deleteFile(existingFile.getFileUrl());
		}
		List<CommunityReport> communityReports = communityReportRepository.findByCommunity_Id(communityId);
		communityReportRepository.deleteAll(communityReports);

		photoRepository.deleteByCommunityId(communityId);
		communityRepository.deleteById(communityId);
	}

	@Transactional
	public ViewAndCommentResponse getCommentAndViewByCommunity(Long communityId) {
		Community community = communityRepository.getById(communityId);
		return ViewAndCommentResponse.from(community);
	}
}