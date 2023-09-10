package com.sunny.backend.service.comment;


import com.amazonaws.services.kms.model.NotFoundException;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.CustomException;
import com.sunny.backend.common.ErrorCode;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.dto.request.comment.CommentRequest;
import com.sunny.backend.dto.request.comment.CommentRequestMapper;
import com.sunny.backend.dto.response.comment.CommentResponse;
import com.sunny.backend.entity.Comment;
import com.sunny.backend.entity.Community;
import com.sunny.backend.repository.comment.CommentRepository;
import com.sunny.backend.repository.community.CommunityRepository;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.user.Users;
import com.sunny.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.sunny.backend.common.ErrorCode.COMMENT_NOT_FOUND;
import static com.sunny.backend.common.ErrorCode.COMMUNITY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository usersRepository;
    private final CommunityRepository communityRepository;
    private final CommentRequestMapper commentRequestMapper;
    private final ResponseService responseService;

    //댓글 등록
    @Transactional

    public CommonResponse.SingleResponse<CommentResponse> createComment(CustomUserPrincipal customUserPrincipal,Long communityId, CommentRequest commentRequestDTO) {
        Users user = customUserPrincipal.getUsers();
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CustomException(COMMUNITY_NOT_FOUND));

        Comment comment = commentRequestMapper.toEntity(commentRequestDTO);

        Comment parentComment;
        if (commentRequestDTO.getParentId() != null) {
            parentComment = commentRepository.findById(commentRequestDTO.getParentId())
                    .orElseThrow(() ->new CustomException(COMMENT_NOT_FOUND));
            comment.setParent(parentComment);
        }

        comment.setWriter(user.getName());
        comment.setCommunity(community);
        comment.setContent(commentRequestDTO.getContent());
        comment.setUsers(user);

        commentRepository.save(comment);
        user.getCommentList().add(comment);

        return responseService.getSingleResponse(HttpStatus.OK.value(), new CommentResponse(comment.getId(),comment.getWriter(),comment.getContent()),"댓글을 등록했습니다.");

    }

    //댓글 삭제
    @Transactional
    public CommonResponse deleteComment(CustomUserPrincipal customUserPrincipal, Long commentId) {
        Comment comment = commentRepository.findCommentByIdWithParent(commentId)
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
        if(checkCommentLoginUser(customUserPrincipal,comment)) {
            if (comment.getChildren().size() != 0) { // 자식이 있으면 상태만 변경
                comment.changeIsDeleted(true);
            } else { // 삭제 가능한 조상 댓글을 구해서 삭제
                commentRepository.delete(getDeletableAncestorComment(comment));
            }
        }

        return responseService.getGeneralResponse(HttpStatus.OK.value(), "댓글을 삭제 하였습니다..");
    }

    //조상 댓글 있는지 check
    private Comment getDeletableAncestorComment(Comment comment) {
        Comment parent = comment.getParent(); // 현재 댓글의 부모를 구함
        if(parent != null && parent.getChildren().size() == 1 && parent.getIsDeleted())
            // 부모가 있고, 부모의 자식이 1개(지금 삭제하는 댓글)이고, 부모의 삭제 상태가 TRUE인 댓글이라면 재귀
            return getDeletableAncestorComment(parent);
        return comment; // 삭제해야하는 댓글 반환
    }

    @Transactional
    public CommonResponse updateComment(CustomUserPrincipal customUserPrincipal, Long commentId, CommentRequest commentRequestDTO) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
        if(checkCommentLoginUser(customUserPrincipal,comment)) {
            comment.setContent(commentRequestDTO.getContent());
        }
        return responseService.getSingleResponse(HttpStatus.OK.value(), new CommentResponse(comment.getId(),comment.getWriter(),comment.getContent()),"댓글을 수정했습니다.");
    }

    //수정 및 삭제 권한 체크
    private boolean checkCommentLoginUser(CustomUserPrincipal customUserPrincipal, Comment comment) {
        if (!Objects.equals(comment.getUsers().getName(), customUserPrincipal.getName())) {
            return false;
        }
        return true;

    }
}