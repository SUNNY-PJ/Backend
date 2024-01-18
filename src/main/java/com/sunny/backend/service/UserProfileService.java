package com.sunny.backend.service;

import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.CommonCustomException;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.dto.response.ProfileResponse;
import com.sunny.backend.dto.response.comment.CommentResponse;
import com.sunny.backend.dto.response.community.CommunityResponse;
import com.sunny.backend.comment.domain.Comment;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.comment.repository.CommentRepository;
import com.sunny.backend.community.repository.CommunityRepository;
import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.sunny.backend.common.CommonErrorCode.COMMUNITY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final CommunityRepository communityRepository;
    private final CommentRepository commentRepository;
    private final ResponseService responseService;

    @Transactional
    public ResponseEntity<CommonResponse.SingleResponse<ProfileResponse>> getUserProfile(
            CustomUserPrincipal customUserPrincipal, Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CommonCustomException(COMMUNITY_NOT_FOUND));

        return responseService.getSingleResponse(
                HttpStatus.OK.value(), new ProfileResponse(community.getUsers().getId(),community.getUsers().getName(),community.getUsers().getProfile()),
                "게시글을 성공적으로 불러왔습니다.");
    }

    @Transactional
    public ResponseEntity<CommonResponse.ListResponse<CommunityResponse.PageResponse>> getFriendsCommunity (CustomUserPrincipal customUserPrincipal, Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CommonCustomException(COMMUNITY_NOT_FOUND));

        List<Community> communityList = communityRepository.findAllByUsers_Id(community.getUsers().getId());
        List<CommunityResponse.PageResponse> communityRes = new ArrayList<>();

        for (Community communities : communityList) {
            communityRes.add(CommunityResponse.PageResponse.from(communities));
        }
        return responseService.getListResponse(HttpStatus.OK.value(), communityRes, "친구가 쓴 작성글 조회");
    }

    public ResponseEntity<CommonResponse.ListResponse<CommentResponse.Mycomment>> getCommentByFriendsId(CustomUserPrincipal customUserPrincipal, Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CommonCustomException(COMMUNITY_NOT_FOUND));
        List<Comment> commentList = commentRepository.findAllByUsers_Id(community.getUsers().getId());
        List<CommentResponse.Mycomment> commentDTOList =
                commentList.stream()
                    .map(comment -> new CommentResponse.Mycomment(comment.getCommunity().getId(),
                        comment.getId(), comment.getContent(), comment.getUsers().getName(),
                        comment.getCreatedDate(), comment.getUpdatedDate()))
                        .collect(Collectors.toList());

        return responseService.getListResponse(HttpStatus.OK.value(), commentDTOList, "친구가 쓴 댓글 조회");
    }
}
