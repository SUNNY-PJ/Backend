package com.sunny.backend.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.dto.response.community.CommunityResponse;
import com.sunny.backend.entity.Community;
import com.sunny.backend.entity.Scrap;
import com.sunny.backend.repository.ScrapRepository;
import com.sunny.backend.repository.community.CommunityRepository;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.user.Users;
import com.sunny.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScrapService {
    private final UserRepository userRepository;
    private final ScrapRepository scrapRepository;
    private final CommunityRepository communityRepository;
    private final ResponseService responseService;

    //스크랩 조회
    public CommonResponse getScrapsByUserId(CustomUserPrincipal customUserPrincipal) {
        Users user = userRepository.findById(customUserPrincipal.getId()).orElseThrow(
                () -> new NotFoundException("user not found"));

        List<Scrap> scrapList = scrapRepository.findAllByUsers_Id(user.getId()); //user id 이용해서 전체 스크랩 조회

        List<CommunityResponse> communityResponseList = scrapList.stream()
                .map(scrap -> new CommunityResponse(scrap.getCommunity()))
                .collect(Collectors.toList());

        return responseService.getListResponse(HttpStatus.OK.value(), communityResponseList);
    }

    //스크랩 추가
    public CommonResponse addScrapToCommunity(CustomUserPrincipal customUserPrincipal, Long communityId) {
        Users user = userRepository.findById(customUserPrincipal.getId()).orElseThrow(
                () -> new NotFoundException("user not found"));

        Community community = communityRepository.findById(communityId)
                .orElseThrow(()->new NotFoundException("could not found community"));
        Scrap scrap=Scrap.builder()
                .community(community)
                .users(user)
                .build();
        scrapRepository.save(scrap);

        return responseService.getGeneralResponse(HttpStatus.OK.value(), "스크랩하였습니다.");

    }

    //스크랩 취소
    public CommonResponse removeScrapFromCommunity(CustomUserPrincipal customUserPrincipal, Long communityId) {

        try {
            Users user = userRepository.findById(customUserPrincipal.getId()).orElseThrow(
                    () -> new NotFoundException("user not found"));


            Community community = communityRepository.findById(communityId)
                    .orElseThrow(()->new NotFoundException("could not found community"));


            Scrap deleteScrap = scrapRepository.findByUsersAndCommunity(user, community);
            if (deleteScrap != null) {
                scrapRepository.delete(deleteScrap);
                return responseService.getGeneralResponse(HttpStatus.OK.value(), "스크랩 게시글이 삭제 되었습니다.");
            } else {
                return responseService.getGeneralResponse(HttpStatus.NOT_FOUND.value(), "스크랩 게시글을 찾을 수 없습니다.");
            }

        } catch (Exception e) {
            return responseService.getGeneralResponse(HttpStatus.BAD_REQUEST.value(),"잘못된 요청입니다.");
        }
    }
}






