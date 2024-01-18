package com.sunny.backend.comment.service;

import static com.sunny.backend.comment.domain.Comment.validateCommentByUser;
import static com.sunny.backend.common.CommonErrorCode.*;

import com.sunny.backend.community.repository.CommunityRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.CommonCustomException;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.dto.request.comment.CommentRequest;
import com.sunny.backend.dto.request.comment.CommentRequestMapper;
import com.sunny.backend.dto.response.comment.CommentResponse;
import com.sunny.backend.comment.domain.Comment;

import com.sunny.backend.comment.repository.CommentRepository;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.user.domain.Users;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final CommunityRepository communityRepository;
	private final CommentRequestMapper commentRequestMapper;
	private final ResponseService responseService;


	private CommentResponse mapCommentToResponse(Comment comment, Users currentUser) {
		boolean isPrivate = comment.getIsPrivated();
		CommentResponse commentResponse;
		if (isPrivate && !(currentUser.getId() == comment.getUsers().getId() ||
				currentUser.getId() == comment.getCommunity().getUsers().getId())) {
			commentResponse = new CommentResponse(comment.getId(), comment.getUsers().getName(),
					"비밀 댓글입니다.", comment.getCreatedDate(), comment.getUpdatedDate());
		} else {
			commentResponse = new CommentResponse(
					comment.getId(),
					comment.getUsers().getName(),
					comment.getContent(),
					comment.getCreatedDate(),
					comment.getUpdatedDate()
			);
		}
		commentResponse.setChildren(comment.getChildren()
				.stream()
				.map(childComment -> mapCommentToResponse(childComment, currentUser))
				.toList()
		);
		return commentResponse;
	}
	@Transactional
	public ResponseEntity<CommonResponse.ListResponse<CommentResponse>> getCommentList(
			CustomUserPrincipal customUserPrincipal, Long communityId) {
		Users user = customUserPrincipal.getUsers();
		Community community = communityRepository.findById(communityId)
				.orElseThrow(() -> new CommonCustomException(COMMUNITY_NOT_FOUND));
		List<Comment> comments = commentRepository.findAllByCommunity_Id(communityId);
		List<CommentResponse> commentResponses = comments.stream()
				.filter(comment -> comment.getParent() == null)
				.map(comment -> mapCommentToResponse(comment, user))
				.toList();
		return responseService.getListResponse(HttpStatus.OK.value(), commentResponses, "댓글을 조회했습니다.");
	}
	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<CommentResponse>> createComment(
			CustomUserPrincipal customUserPrincipal, Long communityId, CommentRequest commentRequestDTO) {
		Users user = customUserPrincipal.getUsers();
		Community community = communityRepository.findById(communityId)
				.orElseThrow(() -> new CommonCustomException(COMMUNITY_NOT_FOUND));

		Comment comment = commentRequestMapper.toEntity(commentRequestDTO);

		Comment parentComment = null;
		if (commentRequestDTO.getParentId() != null) {
			parentComment = commentRepository.findById(commentRequestDTO.getParentId())
					.orElseThrow(() -> new CommonCustomException(COMMENT_NOT_FOUND));
			if (parentComment.getParent() != null) {
				throw new CommonCustomException(REPLYING_NOT_ALLOWED);
			}
			comment.setParent(parentComment);
		}
		comment.setCommunity(community);
		comment.setContent(commentRequestDTO.getContent());
		comment.setUsers(user);

		boolean isPrivate = commentRequestDTO.getIsPrivated();
		comment.setIsPrivated(isPrivate);
		commentRepository.save(comment);
		user.addComment(comment);
		return responseService.getSingleResponse(HttpStatus.OK.value(),
				new CommentResponse(comment.getId(), comment.getUsers().getName(), comment.getContent(),
						comment.getCreatedDate(), comment.getUpdatedDate()), "댓글을 등록했습니다.");

	}

	@Transactional
	public ResponseEntity<CommonResponse.GeneralResponse> deleteComment(
			CustomUserPrincipal customUserPrincipal, Long commentId) {
		Comment comment = commentRepository.findCommentByIdWithParent(commentId)
				.orElseThrow(() -> new CommonCustomException(COMMENT_NOT_FOUND));
		validateCommentByUser(customUserPrincipal.getUsers().getId(),comment.getUsers().getId());
			if (comment.getChildren().size() != 0) {
				comment.changeIsDeleted(true);
			} else {
				commentRepository.delete(getDeletableAncestorComment(comment));
		}
		return responseService.getGeneralResponse(HttpStatus.OK.value(), "댓글을 삭제 하였습니다.");
	}

	private Comment getDeletableAncestorComment(Comment comment) {
		Comment parent = comment.getParent();
		if (parent != null && parent.getChildren().size() == 1 && parent.getIsDeleted())
			return getDeletableAncestorComment(parent);
		return comment;
	}

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<CommentResponse>> updateComment(
			CustomUserPrincipal customUserPrincipal, Long commentId, CommentRequest commentRequestDTO) {

		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new CommonCustomException(COMMENT_NOT_FOUND));
		validateCommentByUser(customUserPrincipal.getUsers().getId(),comment.getUsers().getId());
		boolean isPrivate = commentRequestDTO.getIsPrivated();
		comment.setIsPrivated(isPrivate);
		return responseService.getSingleResponse(HttpStatus.OK.value(),
				new CommentResponse(comment.getId(), comment.getUsers().getName(), comment.getContent(),
						comment.getCreatedDate(), comment.getUpdatedDate()), "댓글을 수정했습니다.");
	}
}