
package com.sunny.backend.service.community;

import com.amazonaws.services.kms.model.NotFoundException;
import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.CustomException;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.dto.request.community.CommunityRequest;
import com.sunny.backend.dto.response.community.CommunityResponse;
import com.sunny.backend.entity.*;
import com.sunny.backend.repository.photo.PhotoRepository;
import com.sunny.backend.repository.community.CommunityRepository;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.service.S3Service;
import com.sunny.backend.user.Users;
import com.sunny.backend.user.repository.UserRepository;
import com.sunny.backend.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.sunny.backend.common.ErrorCode.COMMUNITY_NOT_FOUND;
import static com.sunny.backend.common.ErrorCode.NO_USER_PERMISSION;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final ResponseService responseService;
    private final S3Service s3Service;
    private final RedisUtil redisUtil;


    //게시글 상세 조회
    @Transactional
    public CommonResponse.SingleResponse<CommunityResponse> findCommunity(CustomUserPrincipal customUserPrincipal, Long communityId){
        Users users = customUserPrincipal.getUsers();
        Community community=communityRepository.findById(communityId)
                .orElseThrow(()->new CustomException(COMMUNITY_NOT_FOUND));
        String viewCount = redisUtil.getData(String.valueOf(users.getId()));
        System.out.println(viewCount);
        if (viewCount == null) {

            redisUtil.setDateExpire(String.valueOf(users.getId()), communityId + "_", calculateTimeUntilMidnight());
            community.increaseView();
        } else {
            String[] strArray = viewCount.split("_");
            List<String> redisBoardList = Arrays.asList(strArray);

            boolean isView = false;

            if (!redisBoardList.isEmpty()) {
                for (String redisBoardId : redisBoardList) {
                    if (String.valueOf(communityId).equals(redisBoardId)) {
                        isView = true;
                        break;
                    }
                }
                if (!isView) {
                    viewCount += communityId + "_";

                    redisUtil.setDateExpire(String.valueOf(users.getId()), viewCount, calculateTimeUntilMidnight());
                    community.updateView();
                }
            }
        }
        return responseService.getSingleResponse(HttpStatus.OK.value(), new CommunityResponse(community),"게시글을 성공적으로 불러왔습니다. ");
    }

    public static long calculateTimeUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.truncatedTo(ChronoUnit.DAYS).plusDays(1);
        return ChronoUnit.SECONDS.between(now, midnight);
    }

    @Transactional
    public CommonResponse.SingleResponse<CommunityResponse> createCommunity(CustomUserPrincipal customUserPrincipal, CommunityRequest communityRequest, List<MultipartFile> multipartFileList) {
        Users user = customUserPrincipal.getUsers();
        Community community= Community.builder()
                .title(communityRequest.getTitle())
                .contents(communityRequest.getContents())
                .writer(user.getName())
                .boardType(communityRequest.getType())
                .users(user)
                .build();
        user.getCommunityList().add(community);

        if (multipartFileList!=null) {
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
        Community savedCommunity=communityRepository.save(community);
        return responseService.getSingleResponse(HttpStatus.OK.value(), new CommunityResponse(savedCommunity),"게시글을 성공적으로 작성했습니다. ");
    }

    //게시판 조회
    //To do  -> Slice 찾아보고 수정
    //단순 조회
    @Transactional
    public PageImpl<CommunityResponse.PageResponse> getCommunityList(Pageable pageable) {
        PageImpl<CommunityResponse.PageResponse> result = communityRepository.getCommunityList(pageable);
        return result;
    }
    //검색 조건 추가해서 조회
    public PageImpl<CommunityResponse.PageResponse> getPageListWithSearch(SortType sortType,BoardType boardType, SearchType searchCondition, Pageable pageable) {
        PageImpl<CommunityResponse.PageResponse> result = communityRepository.getPageListWithSearch(sortType,boardType, searchCondition, pageable);
        return result;
    }

    //게시글 조회
    @Transactional
    public CommonResponse.SingleResponse getCommunity(CustomUserPrincipal customUserPrincipal, Long communityId){
        Users user = customUserPrincipal.getUsers();
        Community community=communityRepository.findById(communityId)
                .orElseThrow(()-> new NotFoundException("could not found Community"));

        return responseService.getSingleResponse(HttpStatus.OK.value(), new CommunityResponse(community),"게시글 목록을 불러왔습니다.");
    }

    //게시글 수정
    @Transactional
    public CommonResponse.SingleResponse updateCommunity(CustomUserPrincipal customUserPrincipal, Long communityId, CommunityRequest communityRequest , List<MultipartFile> files) {
        //To do : error 처리
        Users user = customUserPrincipal.getUsers();
        Community community = communityRepository.findById(communityId).orElseThrow(() -> new NotFoundException("Community Post not found!"));
        System.out.println(community);
        if (checkCommunityLoginUser(customUserPrincipal,community)) {
            new CustomException(COMMUNITY_NOT_FOUND);
        }


        community.updateCommunity(communityRequest);
        communityRepository.save(community);


        if (!files.isEmpty()) {
            List<Photo> existingPhotos = photoRepository.findByCommunityId(communityId);
            // 기존 photo 삭제
            photoRepository.deleteAll(existingPhotos);

            for(Photo photo : existingPhotos){
                s3Service.deleteFile(photo.getFileUrl());
            }

            //새롭게 등록
            List<Photo> photoList = new ArrayList<>();
            for (MultipartFile multipartFile : files) {
                Photo photo=Photo.builder()
                        .filename(multipartFile.getOriginalFilename())
                        .fileSize(multipartFile.getSize())
                        .fileUrl(s3Service.upload(multipartFile))
                        .community(community)
                        .build();
                photoList.add(photo);
            }
            photoRepository.saveAll(photoList);
        }
        user.getCommunityList().add(community);
        userRepository.save(user);

        return responseService.getSingleResponse(HttpStatus.OK.value(), new CommunityResponse(community),"게시글 수정을 완료했습니다.");
    }

    //게시글 삭제
    @Transactional
    public CommonResponse.SingleResponse deleteCommunity(CustomUserPrincipal customUserPrincipal, Long communityId) {

        //To do : error 처리
        Users user = customUserPrincipal.getUsers();
        Community community = communityRepository.findById(communityId).orElseThrow(() -> new NotFoundException("Community post  not found!"));
        List<Photo> photoList=photoRepository.findByCommunityId(communityId);
        if (checkCommunityLoginUser(customUserPrincipal,community)) {
            new CustomException(COMMUNITY_NOT_FOUND);
        }

        for (Photo existingFile : photoList) {
            s3Service.deleteFile(existingFile.getFileUrl());
        }


        photoRepository.deleteByCommunityId(communityId);
        communityRepository.deleteById(communityId);

        return responseService.getSingleResponse(HttpStatus.OK.value(), new CommunityResponse(community),"게시글을 삭제했습니다.");
    }
    //수정 및 삭제 권한 체크
    private boolean checkCommunityLoginUser(CustomUserPrincipal customUserPrincipal, Community community) {
        if (!Objects.equals(customUserPrincipal.getName(), community.getWriter())) {
            return false;
        }
        return true;

    }


}
