package com.sunny.backend.controller;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.config.AuthUser;
import com.sunny.backend.dto.request.CompetitionRequestDto;
import com.sunny.backend.dto.response.CompetitionResponseDto;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.service.CompetitionService;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "3. Competition", description = "Competition API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/compettion")
public class CompetitionController {

    private final CompetitionService competitionService;

    @ApiOperation(tags = "3. Competition", value = "친구에게 경쟁 신청")
    @PostMapping("")
    public ResponseEntity<CommonResponse.GeneralResponse> applyCompetition(@AuthUser CustomUserPrincipal customUserPrincipal,
                                             @RequestBody CompetitionRequestDto.CompetitionApply competitionApply) {
        return ResponseEntity.ok(competitionService.applyCompetition(customUserPrincipal, competitionApply));
    }

    @ApiOperation(tags = "3. Competition", value = "경쟁 승인하기")
    @PostMapping("/accept")
    public ResponseEntity<CommonResponse.GeneralResponse> acceptCompetition(@AuthUser CustomUserPrincipal customUserPrincipal,
        @RequestBody CompetitionRequestDto.CompetitionAccept competitionAccept) {
        return ResponseEntity.ok(competitionService.acceptCompetition(customUserPrincipal, competitionAccept));
    }

    @ApiOperation(tags = "3. Competition", value = "경쟁 상태 가져오기")
    @GetMapping("/status/{competition_id}")
    public ResponseEntity<CommonResponse.SingleResponse<CompetitionResponseDto.CompetitionStatus>> getCompetitionStatus(
        @AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable(name = "competition_id") Long id) {
        return ResponseEntity.ok(competitionService.getCompetitionStatus(customUserPrincipal, id));
    }
}
