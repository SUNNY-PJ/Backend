//package com.sunny.backend.controller;
//
//import com.sunny.backend.dto.request.CommentRequest;
//import com.sunny.backend.dto.request.CommunityRequest;
//import com.sunny.backend.service.CommentService;
//import com.sunny.backend.service.CommunityService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.List;
//
//@RestController
//@RequiredArgsConstructor
//public class CommentController {
//    private final CommentService commentService;
//    //카테고리 어떻게 할 지 , 검색 조건 추가
//    //커뮤니티 글 전체 조회
//    @GetMapping("/comment/{communityId}")
//    public ResponseEntity<?> createComment(@PathVariable("communityId") Long communityId, @RequestBody CommentRequest commentRequest) {
//
//        return commentService.insert(communityId,commentRequest);
//    }
////    @PostMapping("")
////    public ResponseEntity createContest(@RequestPart(value = "communityRequest") CommunityRequest communityRequest, @RequestPart("files") List<MultipartFile> files ) throws IOException {
////        if(files ==null){
////            throw new IllegalArgumentException("wrong input image");
////        }
////        return communityService.createContest(communityRequest,files);
////    }
//
//}
