//package com.sunny.backend.controller;
//
//
//import com.sunny.backend.config.AuthUser;
//import com.sunny.backend.dto.request.CommunityRequest;
//import com.sunny.backend.service.CommunityService;
//import com.sunny.backend.user.Users;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import springfox.documentation.annotations.ApiIgnore;
//
//
//import java.io.IOException;
//import java.util.List;
//
//@RestController
//@RequiredArgsConstructor
//@Slf4j
////@RequestMapping("/api") 형식 맞추기
//public class CommunityController {
//    private final CommunityService communityService;
//    //카테고리 어떻게 할 지 , 검색 조건 추가
//    //커뮤니티 글 전체 조회
//    @GetMapping("")
//    public ResponseEntity getCommunityList(@PageableDefault Pageable pageable, @RequestParam(required = false)String title,@RequestParam(required = false)String contents ) {
//
//        return communityService.getCommunityList(pageable,title,contents);
//    }
//    @PostMapping("")
//    public ResponseEntity createContest(@ApiIgnore @AuthUser Users users, @RequestPart(value = "communityRequest") CommunityRequest communityRequest, @RequestPart("files") List<MultipartFile> files ) throws IOException {
//        if(files ==null){
//            throw new IllegalArgumentException("wrong input image");
//        }
//        return communityService.createContest(users, communityRequest,files);
//    }
//
//
//}
