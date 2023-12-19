package com.sunny.backend.service;

import com.amazonaws.Response;
import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.dto.response.ProfileResponse;
import com.sunny.backend.dto.response.ScrapResponse;
import com.sunny.backend.dto.response.comment.CommentResponse;
import com.sunny.backend.dto.response.community.CommunityResponse;
import com.sunny.backend.entity.Comment;
import com.sunny.backend.entity.Community;
import com.sunny.backend.entity.Photo;
import com.sunny.backend.entity.Scrap;
import com.sunny.backend.repository.ScrapRepository;
import com.sunny.backend.repository.comment.CommentRepository;
import com.sunny.backend.repository.community.CommunityRepository;
import com.sunny.backend.repository.photo.PhotoRepository;
import com.sunny.backend.security.dto.AuthDto;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.user.Users;
import com.sunny.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
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
 * 작성글
 * 작성댓글
 * 프로필 설정 -> 프로필 이미지 바꾸기 , 닉네임 api
 * 로그아웃 -> 일단 잠깐 보류
 * 회원탈퇴
 * 스크랩
 * */

public class MyPageService {
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;
    private final CommentRepository commentRepository;
    private final ResponseService responseService;
    private final ScrapRepository scrapRepository;
    private final PhotoRepository photoRepository;
    private final S3Service s3Service;

    // 내가 쓴 글
    @Transactional
    public ResponseEntity<CommonResponse.ListResponse<CommunityResponse.PageResponse>> getMyCommunity (CustomUserPrincipal customUserPrincipal) {
        Users user = customUserPrincipal.getUsers();
        List<Community> communityList = communityRepository.findAllByUsers_Id(user.getId());
        List<CommunityResponse.PageResponse> communityRes = new ArrayList<>();

        for (Community community : communityList) {
            communityRes.add(new CommunityResponse.PageResponse(community));
        }


        return responseService.getListResponse(HttpStatus.OK.value(), communityRes, "내가 쓴 작성글 조회");
    }
    // 내가 쓴 댓글

    public ResponseEntity<CommonResponse.ListResponse<CommentResponse>> getCommentByUserId(CustomUserPrincipal customUserPrincipal) {
        Users user = customUserPrincipal.getUsers();
        List<Comment> commentList = commentRepository.findAllByUsers_Id(user.getId());
        List<CommentResponse> commentDTOList =
                commentList.stream()
                        .map(comment -> new CommentResponse(comment.getId(), comment.getContent(), comment.getWriter()))
                        .collect(Collectors.toList());
//        List<CommentResponse> commentRes = new ArrayList<>();
        return responseService.getListResponse(HttpStatus.OK.value(), commentDTOList, "내가 쓴 댓글 조회");
    }

    //스크랩 한 글
    @Transactional
    public ResponseEntity<CommonResponse.ListResponse<CommunityResponse>> getScrapByUserId(CustomUserPrincipal customUserPrincipal) {
        Users user = customUserPrincipal.getUsers();
        List<Scrap> scrapList = scrapRepository.findAllByUsers_Id(user.getId());

        List<CommunityResponse> ScrapByCommunity = scrapList.stream()
                .map(scrap -> new CommunityResponse(scrap.getCommunity()))
                .collect(Collectors.toList());


        return responseService.getListResponse(HttpStatus.OK.value(), ScrapByCommunity, "내가 등록한 스크랩 조회");
    }

    @Transactional
    public ResponseEntity<CommonResponse.SingleResponse<ProfileResponse>> updateProfile(CustomUserPrincipal customUserPrincipal, MultipartFile profile, String nickname) {
        Users user = customUserPrincipal.getUsers();

        if (nickname != null) {
            user.setName(nickname);
        }

        if (!profile.isEmpty()) {
            System.out.println("success");
//            s3Service.deleteFile(user.getProfile()); -> 이거 기본 프로필 지정한 뒤에 해야함
            user.setProfile(s3Service.upload(profile));
            System.out.println("User Profile:" + user.getProfile());
        }

        userRepository.save(user);

        return responseService.getSingleResponse(HttpStatus.OK.value(), new ProfileResponse(user.getName(), user.getProfile()), "프로필 변경 성공");
    }
    //알림 설정?

    //로그아웃, 회원 탈퇴 (로그아웃은 카톡에서 해야되는 걸로 해야 하나?)
}
