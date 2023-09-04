package com.sunny.backend.controller;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.config.AuthUser;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.service.ScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scrap")
@RequiredArgsConstructor
public class ScrapController {
    private final ScrapService scrapService;


    @GetMapping("")
    public ResponseEntity<CommonResponse>  getScrapsByUserId(@AuthUser CustomUserPrincipal customUserPrincipal){
        return ResponseEntity.ok().body(scrapService.getScrapsByUserId(customUserPrincipal));
    }

    @PostMapping("/{communityId}")
    public ResponseEntity<CommonResponse> addScrapToCommunity(@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable Long communityId){
        return ResponseEntity.ok().body(scrapService.addScrapToCommunity(customUserPrincipal,communityId));
    }

    @DeleteMapping("/{communityId}")
    public ResponseEntity<CommonResponse> removeScrapFromCommunity(@AuthUser CustomUserPrincipal customUserPrincipal,@PathVariable Long communityId){
        return ResponseEntity.ok().body(scrapService.removeScrapFromCommunity(customUserPrincipal,communityId));
    }
}
