package com.sunny.backend.controller;


import com.sunny.backend.config.AuthUser;
import com.sunny.backend.dto.request.CommunityRequest;
import com.sunny.backend.dto.response.PageResponse;
import com.sunny.backend.entity.BoardType;
import com.sunny.backend.entity.SearchType;
import com.sunny.backend.service.community.CommunityService;
import com.sunny.backend.service.s3.S3Service;
import com.sunny.backend.user.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {

    private final CommunityService communityService;
    private final S3Service s3Service;


    @GetMapping("")
    public ResponseEntity getCommunityList(@RequestParam(required = false) BoardType boardType,@RequestBody  SearchType searchCondition, Pageable pageable) {
        PageImpl<PageResponse> responseDTO;
        if (boardType == null) {
            // 기본 boardType -> 꿀팁으로 설정
            boardType = BoardType.꿀팁;
        }
        //검색조건 중 모든 내용을 입력하지 않고 요청을 보냈을 때 일반 목록 페이지 출력
        if (searchCondition.getContent().isEmpty() && searchCondition.getWriter().isEmpty() && searchCondition.getTitle().isEmpty()) {
            responseDTO = communityService.getCommunityList(pageable);
        } else {
            responseDTO = communityService.getPageListWithSearch(boardType,searchCondition, pageable);

        }
        return ResponseEntity.ok()
                .body(responseDTO);
    }

    @GetMapping("/{communityId}")
    public ResponseEntity findById(@ApiIgnore @AuthUser Users users, @PathVariable("communityId") Long contestId) {
        return communityService.findById(users,contestId);
    }

    @PostMapping(value = "",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity createCommunity(@ApiIgnore @AuthUser Users users,
                                          @RequestPart(value = "communityRequest") CommunityRequest communityRequest, @RequestPart(required=false ) List<MultipartFile> files ) throws IOException {

        List<String> photoList = new ArrayList<>();

        if (files != null && !files.isEmpty()) { //조건문 빼고?
            photoList = s3Service.upload(files);
        }

        return communityService.createCommunity(users, communityRequest,photoList);
        //return new ResponseEntity(new ApiRes("스터디 등록 성공", HttpStatus.CREATED), HttpStatus.CREATED);
    }
    @PutMapping(value = "/{communityId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
    public ResponseEntity updateCommunity(@ApiIgnore @AuthUser Users users, @PathVariable Long communityId,
                                          @RequestPart(value = "communityRequest") CommunityRequest communityRequest, @RequestPart(required=false ) List<MultipartFile> files) throws IOException {

        List<String> photoList = new ArrayList<>();

        if (files != null && !files.isEmpty()) {
            photoList = s3Service.upload(files); //중복 생길 듯
        }
        return communityService.updateCommunity(users, communityId,communityRequest,photoList);
    }


    @DeleteMapping("/{communityId}/delete")
    public ResponseEntity deleteCommunity(@ApiIgnore @AuthUser Users users, @PathVariable Long communityId) {
        return communityService.deleteCommunity(users, communityId);
    }

}



