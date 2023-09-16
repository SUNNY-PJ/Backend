package com.sunny.backend.controller;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.config.AuthUser;
import com.sunny.backend.dto.request.save.SaveRequest;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.service.save.SaveService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name="6. Save", description = "Save API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/save") //To do : saving 으로 변경?
public class SaveController {
    private final SaveService saveService;

    //절약 목표 설정
    @ApiOperation(tags = "6. Save", value = "절약 목표 등록")
    @PostMapping("")
    public ResponseEntity<CommonResponse.SingleResponse> createSaveGoal(@AuthUser CustomUserPrincipal customUserPrincipal, @RequestBody  SaveRequest saveRequest){
        return ResponseEntity.ok().body(saveService.createSaveGoal(customUserPrincipal,saveRequest));
    }

    @ApiOperation(tags = "6. Save", value = "절약 목표 수정")
    //절약 목표 수정
    @PutMapping("/{savedId}")
    public ResponseEntity<CommonResponse.SingleResponse> updateSaveGoal(@AuthUser CustomUserPrincipal customUserPrincipal,@PathVariable Long savedId,  @RequestBody SaveRequest saveRequest){
        return ResponseEntity.ok().body(saveService.updateSaveGoal(customUserPrincipal,savedId,saveRequest));
    }

}
