package com.sunny.backend.controller;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.config.AuthUser;
import com.sunny.backend.dto.request.CompetitionRequestDto;
import com.sunny.backend.dto.response.CompetitionResponseDto;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.service.CompetitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/compettion")
public class CompetitionController {

    private final CompetitionService competitionService;

    @PostMapping("")
    public ResponseEntity<CommonResponse.GeneralResponse> applyCompetition(@AuthUser CustomUserPrincipal customUserPrincipal,
                                             @RequestBody CompetitionRequestDto.CompetitionApply competitionApply) {
        return ResponseEntity.ok(competitionService.applyCompetition(customUserPrincipal, competitionApply));
    }

    @PostMapping("/accept")
    public ResponseEntity<CommonResponse.GeneralResponse> acceptCompetition(@AuthUser CustomUserPrincipal customUserPrincipal,
        @RequestBody CompetitionRequestDto.CompetitionAccept competitionAccept) {
        return ResponseEntity.ok(competitionService.acceptCompetition(customUserPrincipal, competitionAccept));
    }

    @GetMapping("/status/{competition_id}")
    public ResponseEntity<CommonResponse.SingleResponse<CompetitionResponseDto.CompetitionStatus>> getCompetitionStatus(
        @AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable(name = "competition_id") Long id) {
        return ResponseEntity.ok(competitionService.getCompetitionStatus(customUserPrincipal, id));
    }
}
