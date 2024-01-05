package com.sunny.backend.service.community;

import static com.sunny.backend.common.CommonErrorCode.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.sunny.backend.entity.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.kms.model.NotFoundException;
import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.CommonCustomException;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.dto.request.community.CommunityRequest;
import com.sunny.backend.dto.response.community.CommunityResponse;
import com.sunny.backend.repository.community.CommunityRepository;
import com.sunny.backend.repository.photo.PhotoRepository;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.service.S3Service;
import com.sunny.backend.user.Users;
import com.sunny.backend.util.RedisUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityService {

	private final CommunityRepository communityRepository;
	private final PhotoRepository photoRepository;
	private final ResponseService responseService;
	private final S3Service s3Service;
	private final RedisUtil redisUtil;

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<CommunityResponse>> findCommunity(
			CustomUserPrincipal customUserPrincipal, Long communityId) {
		Users users = customUserPrincipal.getUsers();
		Community community = communityRepository.findById(communityId)
				.orElseThrow(() -> new CommonCustomException(COMMUNITY_NOT_FOUND));

		String viewCount = redisUtil.getData(String.valueOf(users.getId()));

		if (StringUtils.isBlank(viewCount)) {
			redisUtil.setDateExpire(String.valueOf(users.getId()), communityId + "_", calculateTimeUntilMidnight());
			community.increaseView();
		} else {
			List<String> redisBoardList = Arrays.asList(viewCount.split("_"));

			boolean isViewed = redisBoardList.contains(String.valueOf(communityId));

			if (!isViewed) {
				viewCount += communityId + "_";
				redisUtil.setDateExpire(String.valueOf(users.getId()), viewCount, calculateTimeUntilMidnight());
				community.updateView();
			}
		}
		return responseService.getSingleResponse(
				HttpStatus.OK.value(), new CommunityResponse(community,false),
				"게시글을 성공적으로 불러왔습니다.");
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
				.writer(user.getName())
				.boardType(communityRequest.getType())
				.users(user)
				.build();

		if (multipartFileList != null && !multipartFileList.isEmpty()) {
			List<Photo> photoList = new ArrayList<>();
			for (MultipartFile multipartFile : multipartFileList) {
				Photo photo = Photo.builder()
						.filename(multipartFile.getOriginalFilename())
						.fileSize(multipartFile.getSize())
						.fileUrl(s3Service.upload(multipartFile))
						.community(community)
						.build();
				photoList.add(photo);
			}
			photoRepository.saveAll(photoList);
			community.addPhoto(photoList);
		}
		communityRepository.save(community);

		if (user.getCommunityList() == null) {
			user.addCommunity(community);
		}
		return responseService.getSingleResponse(HttpStatus.OK.value(), new CommunityResponse(community,false),
				"게시글을 성공적으로 작성했습니다. ");
	}


	//게시판 조회
	@Transactional(readOnly = true)
	public Slice<CommunityResponse.PageResponse> getCommunityList(Pageable pageable) {
		Slice<CommunityResponse.PageResponse> result = communityRepository.getCommunityList(pageable);
		return result;
	}

	//검색 조건 추가해서 조회
	public Slice<CommunityResponse.PageResponse> getPageListWithSearch(SortType sortType,BoardType boardType, String searchText, Pageable pageable) {
		Slice<CommunityResponse.PageResponse> result = communityRepository.getPageListWithSearch(sortType,boardType,searchText, pageable);
		return result;
	}

	//게시글 수정
	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<CommunityResponse>> updateCommunity(
			CustomUserPrincipal customUserPrincipal, Long communityId,
			CommunityRequest communityRequest, List<MultipartFile> files) {
		Users user = customUserPrincipal.getUsers();
		Community community = communityRepository.findById(communityId)
				.orElseThrow(() -> new NotFoundException("Community Post not found!"));
		boolean isModified=true;
		community.validateCommunityByUser(user.getId(), community.getId());
		community.getPhotoList().clear();
		community.updateCommunity(communityRequest);
		community.updateModifiedAt(LocalDateTime.now());


		if (files != null && !files.isEmpty()) {
			List<Photo> existingPhotos = photoRepository.findByCommunityId(communityId);
			// 기존 photo 삭제
			photoRepository.deleteAll(existingPhotos);

			for (Photo photo : existingPhotos) {
				s3Service.deleteFile(photo.getFileUrl());
			}
			//새롭게 등록
			List<Photo> photoList = new ArrayList<>();
			for (MultipartFile multipartFile : files) {
				Photo photo = Photo.builder()
						.filename(multipartFile.getOriginalFilename())
						.fileSize(multipartFile.getSize())
						.fileUrl(s3Service.upload(multipartFile))
						.community(community)
						.build();
				photoList.add(photo);
			}
			photoRepository.saveAll(photoList);
			community.addPhoto(photoList);
		}
		return responseService.getSingleResponse(HttpStatus.OK.value(), new CommunityResponse(community,isModified),
				"게시글 수정을 완료했습니다.");
	}

	//게시글 삭제
	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<CommunityResponse>> deleteCommunity(
			CustomUserPrincipal customUserPrincipal, Long communityId) {

		//To do : error 처리
		Users user = customUserPrincipal.getUsers();
		Community community = communityRepository.findById(communityId)
				.orElseThrow(() -> new NotFoundException("Community post  not found!"));
		List<Photo> photoList = photoRepository.findByCommunityId(communityId);
		community.validateCommunityByUser(user.getId(), community.getId());
		for (Photo existingFile : photoList) {
			s3Service.deleteFile(existingFile.getFileUrl());
		}
		photoRepository.deleteByCommunityId(communityId);
		communityRepository.deleteById(communityId);

		return responseService.getSingleResponse(HttpStatus.OK.value(), new CommunityResponse(community,false),
				"게시글을 삭제했습니다.");
	}

}