//package com.sunny.backend.controller;
//
//import com.sunny.backend.dto.request.CommunityRequest;
//import com.sunny.backend.dto.request.ConsumptionRequest;
//import com.sunny.backend.service.CommunityService;
//import com.sunny.backend.service.ConsumService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
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
//@Slf4j
//@RequiredArgsConstructor
//public class ConsumptionController {
//    private final ConsumService comsumService;
//    //카테고리 어떻게 할 지 , 검색 조건 추가
//    //커뮤니티 글 전체 조회
//    @GetMapping("")
//    //Auth user만 추가
//    public ResponseEntity getConsumptionList() {
//
//        return comsumService.getConsumptionList();
//    }
//    @PostMapping("")
//    public ResponseEntity createConsumption(@RequestPart(value = "comsumptionRequest") ConsumptionRequest consumtionRequest ) throws IOException {
//
//        return comsumService.createConsumption(comsumptionRequest);
//    }
//
//}
