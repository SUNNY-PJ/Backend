package com.sunny.backend.service;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.dto.response.ProfileResponse;
import com.sunny.backend.dto.response.comment.CommentResponse;
import com.sunny.backend.dto.response.community.CommunityResponse;
import com.sunny.backend.scrap.repository.ScrapRepository;
import com.sunny.backend.comment.domain.Comment;
import com.sunny.backend.scrap.domain.Scrap;
import com.sunny.backend.repository.comment.CommentRepository;
import com.sunny.backend.community.repository.CommunityRepository;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.user.Users;
import com.sunny.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

/**
 * TODO
 * 회원탈퇴
 * */
@Transactional
public class MyPageService {
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;
    private final CommentRepository commentRepository;
    private final ResponseService responseService;
    private final ScrapRepository scrapRepository;
    private final S3Service s3Service;

    // 마이페이지 조회
    public ResponseEntity<CommonResponse.SingleResponse<ProfileResponse>> getMypage(
            CustomUserPrincipal customUserPrincipal) {
        Users user = customUserPrincipal.getUsers();
        ProfileResponse profileResponse = ProfileResponse.from(user);
        return responseService.getSingleResponse(HttpStatus.OK.value(), profileResponse, "프로필 조회 성공");
    }

    // 내가 쓴 글
    public ResponseEntity<CommonResponse.ListResponse<CommunityResponse.PageResponse>> getMyCommunity(
            CustomUserPrincipal customUserPrincipal) {
        Users user = customUserPrincipal.getUsers();
        List<Community> communityList = communityRepository.findAllByUsers_Id(user.getId());
        List<CommunityResponse.PageResponse> communityRes = new ArrayList<>();
        for (Community community : communityList) {
            communityRes.add(CommunityResponse.PageResponse.from(community));
        }
        return responseService.getListResponse(HttpStatus.OK.value(), communityRes, "내가 쓴 작성글 조회");
    }

    // 내가 쓴 댓글
    @Transactional(readOnly = true)
    public ResponseEntity<CommonResponse.ListResponse<CommentResponse.Mycomment>> getCommentByUserId(CustomUserPrincipal customUserPrincipal) {
        Users user = customUserPrincipal.getUsers();
        List<Comment> commentList = commentRepository.findAllByUsers_Id(user.getId());
        List<CommentResponse.Mycomment> commentDTOList =
                commentList.stream()
                    .map(comment -> new CommentResponse.Mycomment(comment.getCommunity().getId(),
                        comment.getId(),  comment.getUsers().getName(),comment.getContent(),
                        comment.getCreatedDate(), comment.getUpdatedDate()))
                        .collect(Collectors.toList());

        return responseService.getListResponse(HttpStatus.OK.value(), commentDTOList, "내가 쓴 댓글 조회");
    }

    //스크랩 한 글
    public ResponseEntity<CommonResponse.ListResponse<CommunityResponse>> getScrapByUserId(CustomUserPrincipal customUserPrincipal) {
        Users user = customUserPrincipal.getUsers();
        List<Scrap> scrapList = scrapRepository.findAllByUsers_Id(user.getId());
        List<CommunityResponse> scrapByCommunity = scrapList.stream()
            .map(scrap -> {
                scrap = scrapRepository.findByUsersAndCommunity(user, scrap.getCommunity());
                boolean isScrapedByCurrentUser = (scrap != null);
                return CommunityResponse.of(scrap.getCommunity(), false, isScrapedByCurrentUser);
            })
            .toList();

        return responseService.getListResponse(HttpStatus.OK.value(), scrapByCommunity,
            "내가 등록한 스크랩 조회");
    }

    public ResponseEntity<CommonResponse.SingleResponse<ProfileResponse>> updateProfile(
        CustomUserPrincipal customUserPrincipal, MultipartFile profile) {

        Users user = customUserPrincipal.getUsers();
        // 새 프로필 업로드
        if (profile != null && !profile.isEmpty()) {
            String uploadedProfileUrl = s3Service.upload(profile);
            user.setProfile(uploadedProfileUrl);
        } else if (profile == null) {
            user.setProfile("https://sunny-pj.s3.ap-northeast-2.amazonaws.com/Profile+Image.png");
        }
        ProfileResponse profileResponse = ProfileResponse.from(user);
        userRepository.save(user);

        return responseService.getSingleResponse(HttpStatus.OK.value(), profileResponse, "프로필 변경 완료");
    }

    public ResponseEntity<CommonResponse.GeneralResponse> deleteAccount(
            CustomUserPrincipal customUserPrincipal) {
        Users user = customUserPrincipal.getUsers();
        userRepository.deleteById(user.getId());
        return responseService.getGeneralResponse(HttpStatus.OK.value(), "성공적으로 탈퇴 되었습니다.");
    }
}