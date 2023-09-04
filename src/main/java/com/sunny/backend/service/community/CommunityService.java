
package com.sunny.backend.service.community;

import com.amazonaws.services.kms.model.NotFoundException;
import com.sunny.backend.common.CommonResponse;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final ResponseService responseService;
    private final S3Service s3Service;

    //게시글 등록
    @Transactional
    public CommonResponse.SingleResponse<CommunityResponse> createCommunity(CustomUserPrincipal customUserPrincipal, CommunityRequest communityRequest, List<MultipartFile> multipartFileList) {

        Users user = userRepository.findById(customUserPrincipal.getId()).orElseThrow(
                () -> new NotFoundException("user not found"));
        Community community= Community.builder()
                .title(communityRequest.getTitle())
                .contents(communityRequest.getContents())
                .writer(user.getName())
                .boardType(communityRequest.getType())
                .users(user)
                .build();
        user.getCommunityList().add(community);
        communityRepository.save(community);

        List<Photo> photoList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFileList) {
            Photo photo=Photo.builder()
                    .filename(multipartFile.getOriginalFilename())
                    .fileSize(multipartFile.getSize())
                    .fileUrl(s3Service.upload(multipartFile))
                    .community(community)
                    .build();
            photoList.add(photo);
        }
        photoRepository.saveAll(photoList);
        return responseService.getSingleResponse(HttpStatus.OK.value(), new CommunityResponse(community));
    }

    //게시판 조회
    //To do  -> Slice 찾아보고 수정
    //단순 조회
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

    public CommonResponse getCommunity(CustomUserPrincipal customUserPrincipal, Long communityId){
        Users user=userRepository.findById(customUserPrincipal.getId())
                .orElseThrow(()-> new NotFoundException("could not found user"));
        Community community=communityRepository.findById(communityId)
                .orElseThrow(()-> new NotFoundException("could not found Community"));

        return responseService.getSingleResponse(HttpStatus.OK.value(), new CommunityResponse(community));
    }


    //게시글 수정

    @Transactional
    public CommonResponse updateCommunity(CustomUserPrincipal customUserPrincipal, Long communityId, CommunityRequest communityRequest , List<MultipartFile> files) {
        //To do : error 처리
        Users user = userRepository.findById(customUserPrincipal.getId()).orElseThrow(
                () -> new NotFoundException("user not found"));
        Community community = communityRepository.findById(communityId).orElseThrow(() -> new NotFoundException("Community Post not found!"));
        System.out.println(community);
        if (checkCommunityLoginUser(customUserPrincipal,community)) {
            return responseService.getGeneralResponse(HttpStatus.BAD_REQUEST.value(), "게시글 수정 권한이 없습니다.");
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

        return responseService.getSingleResponse(HttpStatus.OK.value(), new CommunityResponse(community));
    }

    //게시글 삭제
    @Transactional
    public CommonResponse deleteCommunity(CustomUserPrincipal customUserPrincipal, Long communityId) {

        //To do : error 처리
        Users user = userRepository.findById(customUserPrincipal.getId()).orElseThrow(
                () -> new NotFoundException("user not found"));
        Community community = communityRepository.findById(communityId).orElseThrow(() -> new NotFoundException("Community post  not found!"));
        List<Photo> photoList=photoRepository.findByCommunityId(communityId);
        if (checkCommunityLoginUser(customUserPrincipal,community)) {
            return responseService.getGeneralResponse(HttpStatus.BAD_REQUEST.value(), "게시글 삭제 권한이 없습니다.");
        }

        for (Photo existingFile : photoList) {
            s3Service.deleteFile(existingFile.getFileUrl());
        }


        photoRepository.deleteByCommunityId(communityId);
        communityRepository.deleteById(communityId);

        return responseService.getSingleResponse(HttpStatus.OK.value(), new CommunityResponse(community));
    }
    //수정 및 삭제 권한 체크
    private boolean checkCommunityLoginUser(CustomUserPrincipal customUserPrincipal, Community community) {
        if (!Objects.equals(customUserPrincipal.getName(), community.getWriter())) {
            return false;
        }
        return true;

    }


}
