//package com.sunny.backend.service;
//
//import com.sunny.backend.dto.request.CommentRequest;
//import com.sunny.backend.dto.request.CommentRequestMapper;
//import com.sunny.backend.dto.response.CommentResponse;
//import com.sunny.backend.dto.response.Response;
//import com.sunny.backend.entity.Comment;
//import com.sunny.backend.entity.Community;
//import com.sunny.backend.repository.comment.CommentRepository;
//import com.sunny.backend.repository.community.CommunityRepository;
//import com.sunny.backend.user.Users;
//import com.sunny.backend.user.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@RequiredArgsConstructor
//public class CommentService {
//    private final UserRepository userRepository;
//    private final CommunityRepository communityRepository;
//    private final CommentRequestMapper commentRequestMapper;
//    private final CommentRepository commentRepository;
//    private final CommentResponse commentResponse;
//    private final Response response;
//    @Transactional //사용에 대해
//    //댓글 등록
//    public ResponseEntity<?> insert(Long communityId, CommentRequest commentRequestDTO) {
//
//        Users users = userRepository.findById(commentRequestDTO.getUserId())
//                .orElseThrow(() -> new IllegalArgumentException("Could not found user id : " + commentRequestDTO.getUserId()));
//
//        Community community = communityRepository.findById(communityId)
//                .orElseThrow(() -> new IllegalArgumentException("Could not found community id : " + communityId));
//        Comment comment = commentRequestMapper.toEntity(commentRequestDTO); //엔티티로 변환
//
//        Comment parentComment;
//        if (commentRequestDTO.getParentId() != null) {
//            parentComment = commentRepository.findById(commentRequestDTO.getParentId())
//                    .orElseThrow(() -> new IllegalArgumentException("Could not found comment id : " + commentRequestDTO.getParentId()));
//            comment.updateParent(parentComment);
//        }
//
//
//        comment.setCommunity(community);
//        comment.setContent(commentRequestDTO.getContent());
//        comment.updateWriter(users);
//        comment.updateCommunity(community);
//
//
//        commentRepository.save(comment);
//        return response.success(new CommentResponse(comment.getId(),comment.getContent(),comment.getUsers().getName()), "댓글 등록 성공", HttpStatus.CREATED);
//
//    }
//    //댓글 삭제
//    @Transactional
//    public void delete(Long commentId) {
//        Comment comment = commentRepository.findCommentByIdWithParent(commentId)
//                .orElseThrow(() -> new IllegalArgumentException("Could not found comment id : " + commentId));
//        if(comment.getChildren().size() != 0) { // 자식이 있으면 상태만 변경
//            comment.changeIsDeleted(true);
//        } else { // 삭제 가능한 부모 댓글을 구해서 삭제
//            commentRepository.delete(getDeletableAncestorComment(comment));
//        }
//    }
//
//    private Comment getDeletableAncestorComment(Comment comment) {
//        Comment parent = comment.getParent(); // 현재 댓글의 부모를 구함
//        if(parent != null && parent.getChildren().size() == 1 && parent.getIsDeleted())
//            // 부모가 있고, 부모의 자식이 1개(지금 삭제하는 댓글)이고, 부모의 삭제 상태가 TRUE인 댓글이라면 재귀
//            return getDeletableAncestorComment(parent);
//        return comment; // 삭제해야하는 댓글 반환
//    }
//
//    //댓글 수정
//    @Transactional
//    public Comment update(Long commentId, CommentRequest commentRequestDTO) {
//        Comment comment = commentRepository.findById(commentId)
//                .orElseThrow(() -> new IllegalArgumentException("Could not find comment id: " + commentId));
//
//        // 댓글 내용 update
//        comment.setContent(commentRequestDTO.getContent());
//
//
//        return commentRepository.save(comment);
//    }
//
//}
//
