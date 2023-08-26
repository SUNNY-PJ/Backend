
package com.sunny.backend.controller;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.config.AuthUser;
import com.sunny.backend.dto.request.community.CommunityRequest;
import com.sunny.backend.dto.response.community.CommunityResponse;
import com.sunny.backend.entity.BoardType;
import com.sunny.backend.entity.SearchType;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.service.community.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {

    private final CommunityService communityService;
    //게시판 조회
    @GetMapping("")
    public ResponseEntity<PageImpl<CommunityResponse.PageResponse>> getCommunityList(@RequestParam(required = false) BoardType boardType, @RequestBody SearchType searchCondition, Pageable pageable){
        PageImpl<CommunityResponse.PageResponse> responseDTO;
        //검색조건 중 모든 내용을 입력하지 않고 요청을 보냈을 때 일반 목록 페이지 출력
        if (searchCondition.getContent().isEmpty() && searchCondition.getWriter().isEmpty() && searchCondition.getTitle().isEmpty()) {
            responseDTO = communityService.getCommunityList(pageable);
        } else {
            responseDTO = communityService.getPageListWithSearch(boardType,searchCondition, pageable);

        }
        return ResponseEntity.ok().body(responseDTO);
    }

    //게시글 조회
    @GetMapping( "/{communityId}")
    public ResponseEntity<CommonResponse> getCommunity(@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable Long communityId){

        return ResponseEntity.ok().body(communityService.getCommunity(customUserPrincipal,communityId));
    }

    //게시글 등록
    @PostMapping( "")
    public ResponseEntity<CommonResponse> createCommunity(@AuthUser CustomUserPrincipal customUserPrincipal,@RequestPart(value = "communityRequest") CommunityRequest communityRequest , @RequestPart(required = false) List<MultipartFile> files ){
        return ResponseEntity.ok().body(communityService.createCommunity(customUserPrincipal,communityRequest,files));
    }
    //게시글 수정
    @PutMapping("/{communityId}")
    public ResponseEntity<CommonResponse> updateCommunity(@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable Long communityId ,@RequestPart(value = "communityRequest") CommunityRequest communityRequest , @RequestPart(required = false) List<MultipartFile> files ){

        return ResponseEntity.ok().body(communityService.updateCommunity(customUserPrincipal,communityId,communityRequest,files));
    }


    //게시글 삭제
    @DeleteMapping("/{communityId}")
    public ResponseEntity<CommonResponse> deleteCommunity(@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable Long communityId){
        return ResponseEntity.ok().body(communityService.deleteCommunity(customUserPrincipal, communityId));
    }


}

