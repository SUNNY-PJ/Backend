package com.sunny.backend.service.comment;

import static com.sunny.backend.common.CommonErrorCode.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.CommonCustomException;
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
			commentResponse = new CommentResponse(comment.getId(), comment.getWriter(),
					"비밀 댓글입니다.", comment.getCreatedDate(), comment.getUpdatedDate());
		} else {
			commentResponse = new CommentResponse(
					comment.getId(),
					comment.getWriter(),
					comment.getContent(),
					comment.getCreatedDate(),
					comment.getUpdatedDate()
			);
		}
		commentResponse.setChildren(comment.getChildren()
				.stream()
				.map(childComment -> mapCommentToResponse(childComment, currentUser))
				.collect(Collectors.toList())
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
				.collect(Collectors.toList());
		return responseService.getListResponse(HttpStatus.OK.value(), commentResponses, "댓글을 조회했습니다.");
	}
	//댓글 등록
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

		//To do : setter 제외하고 도메인에서 함수로 처리
		comment.setWriter(user.getName());
		comment.setCommunity(community);
		comment.setContent(commentRequestDTO.getContent());
		comment.setUsers(user);

		boolean isPrivate = commentRequestDTO.getIsPrivated();
		comment.setIsPrivated(isPrivate);

		Comment saveComment = commentRepository.save(comment);
		if (user.getCommentList() == null) {
			user.addComment(comment);
		}

		return responseService.getSingleResponse(HttpStatus.OK.value(),
				new CommentResponse(comment.getId(), comment.getWriter(), comment.getContent(),
						comment.getCreatedDate(), comment.getUpdatedDate()), "댓글을 등록했습니다.");

	}

	//댓글 삭제
	@Transactional
	public ResponseEntity<CommonResponse.GeneralResponse> deleteComment(
			CustomUserPrincipal customUserPrincipal, Long commentId) {
		Comment comment = commentRepository.findCommentByIdWithParent(commentId)
			.orElseThrow(() -> new CommonCustomException(COMMENT_NOT_FOUND));
		if (checkCommentLoginUser(customUserPrincipal, comment)) {
			if (comment.getChildren().size() != 0) { // 자식이 있으면 상태만 변경
				comment.changeIsDeleted(true);
			} else { // 삭제 가능한 조상 댓글을 구해서 삭제
				commentRepository.delete(getDeletableAncestorComment(comment));
			}
		}
		return responseService.getGeneralResponse(HttpStatus.OK.value(), "댓글을 삭제 하였습니다.");
	}


	//조상 댓글 있는지 check
	private Comment getDeletableAncestorComment(Comment comment) {
		Comment parent = comment.getParent(); // 현재 댓글의 부모를 구함
		if (parent != null && parent.getChildren().size() == 1 && parent.getIsDeleted())
			// 부모가 있고, 부모의 자식이 1개(지금 삭제하는 댓글)이고, 부모의 삭제 상태가 TRUE인 댓글이라면 재귀
			return getDeletableAncestorComment(parent);
		return comment; // 삭제해야하는 댓글 반환
	}

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<CommentResponse>> updateComment(
		CustomUserPrincipal customUserPrincipal, Long commentId, CommentRequest commentRequestDTO) {

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CommonCustomException(COMMENT_NOT_FOUND));
		if (checkCommentLoginUser(customUserPrincipal, comment)) {
			comment.setContent(commentRequestDTO.getContent());

			boolean isPrivate = commentRequestDTO.getIsPrivated();
			comment.setIsPrivated(isPrivate);
		}
		return responseService.getSingleResponse(HttpStatus.OK.value(),
				new CommentResponse(comment.getId(), comment.getWriter(), comment.getContent(),
						comment.getCreatedDate(), comment.getUpdatedDate()), "댓글을 수정했습니다.");
	}

	//수정 및 삭제 권한 체크
	private boolean checkCommentLoginUser(CustomUserPrincipal customUserPrincipal, Comment comment) {
		if (!Objects.equals(comment.getUsers().getName(), customUserPrincipal.getName())) {
			return false;
		}
		return true;
	}
}