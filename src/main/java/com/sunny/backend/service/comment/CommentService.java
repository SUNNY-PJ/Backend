package com.sunny.backend.service.comment;


import com.amazonaws.services.kms.model.NotFoundException;

import com.sunny.backend.dto.request.CommentRequest;
import com.sunny.backend.dto.request.CommentRequestMapper;
import com.sunny.backend.dto.response.CommentResponse;
import com.sunny.backend.dto.response.Response;
import com.sunny.backend.entity.Comment;
import com.sunny.backend.entity.Community;
import com.sunny.backend.repository.comment.CommentRepository;
import com.sunny.backend.repository.community.CommunityRepository;
import com.sunny.backend.user.Users;
import com.sunny.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final Response response;
    private final CommentRepository commentRepository;
    private final UserRepository usersRepository;
    private final CommunityRepository communityRepository;
    private final CommentRequestMapper commentRequestMapper;

    @Transactional
    public ResponseEntity<?> insertComment(Long contestId, Users user, CommentRequest commentRequestDTO) {

        Users users = usersRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("Could not found user id"));

        Community community = communityRepository.findById(contestId)
                .orElseThrow(() -> new NotFoundException("Could not found contest id  "));

        Comment comment = commentRequestMapper.toEntity(commentRequestDTO);

        Comment parentComment;
        if (commentRequestDTO.getParentId() != null) {
            System.out.println("실행");
            parentComment = commentRepository.findById(commentRequestDTO.getParentId())
                    .orElseThrow(() -> new NotFoundException("Could not found comment id : " + commentRequestDTO.getParentId()));
            comment.setParent(parentComment);
        }

        comment.setWriter(users.getName());
        comment.setCommunity(community);
        comment.setContent(commentRequestDTO.getContent());
        comment.setUsers(users);

        commentRepository.save(comment);
        users.getCommentList().add(comment);

        return response.success(new CommentResponse(comment.getId(),comment.getWriter(),comment.getContent()), "댓글을 등록 하였습니다..", HttpStatus.CREATED);

    }

    @Transactional
    public ResponseEntity<?> deleteComment(Users users, Long commentId) {
        Comment comment = commentRepository.findCommentByIdWithParent(commentId)
                .orElseThrow(() -> new NotFoundException("Could not found comment id : " + commentId));
        if(checkCommentLoginUser(users,comment)) {
            if (comment.getChildren().size() != 0) { // 자식이 있으면 상태만 변경
                comment.changeIsDeleted(true);
            } else { // 삭제 가능한 조상 댓글을 구해서 삭제
                commentRepository.delete(getDeletableAncestorComment(comment));
            }
        }

        return response.success(comment, "댓글을 삭제 하였습니다..", HttpStatus.OK);
    }

    private Comment getDeletableAncestorComment(Comment comment) {
        Comment parent = comment.getParent(); // 현재 댓글의 부모를 구함
        if(parent != null && parent.getChildren().size() == 1 && parent.getIsDeleted())
            // 부모가 있고, 부모의 자식이 1개(지금 삭제하는 댓글)이고, 부모의 삭제 상태가 TRUE인 댓글이라면 재귀
            return getDeletableAncestorComment(parent);
        return comment; // 삭제해야하는 댓글 반환
    }

    @Transactional
    public ResponseEntity<?> updateComment(Users users, Long commentId, CommentRequest commentRequestDTO) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Could not found comment id : " + commentId));
        if(checkCommentLoginUser(users,comment)) {
            comment.setContent(commentRequestDTO.getContent());
        }
        return response.success(comment, "댓글을 수정 하였습니다..", HttpStatus.OK);
    }

    //수정 및 삭제 권한 체크
    private boolean checkCommentLoginUser(Users users, Comment comment) {
        if (!Objects.equals(comment.getUsers().getName(), users.getName())) {
            return false;
        }
        return true;

    }
}