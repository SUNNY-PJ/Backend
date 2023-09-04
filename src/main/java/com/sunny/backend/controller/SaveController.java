package com.sunny.backend.controller;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.config.AuthUser;
import com.sunny.backend.dto.request.save.SaveRequest;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.service.save.SaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/save") //To do : saving 으로 변경?
public class SaveController {
    private final SaveService saveService;

    //절약 목표 설정
    @PostMapping("")
    public ResponseEntity<CommonResponse> createSaveGoal(@AuthUser CustomUserPrincipal customUserPrincipal, @RequestBody  SaveRequest saveRequest){
        return ResponseEntity.ok().body(saveService.createSaveGoal(customUserPrincipal,saveRequest));
    }

    //절약 목표 수정
    @PutMapping("/{savedId}")
    public ResponseEntity<CommonResponse> updateSaveGoal(@AuthUser CustomUserPrincipal customUserPrincipal,@PathVariable Long savedId,  @RequestBody SaveRequest saveRequest){
        return ResponseEntity.ok().body(saveService.updateSaveGoal(customUserPrincipal,savedId,saveRequest));
    }

}
