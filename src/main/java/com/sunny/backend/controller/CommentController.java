package com.sunny.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.config.AuthUser;
import com.sunny.backend.dto.request.comment.CommentRequest;
import com.sunny.backend.dto.response.comment.CommentResponse;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.service.comment.CommentService;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "1. Comment", description = "댓글 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

	private final CommentService commentService;

	@ApiOperation(tags = "1. Comment", value = "댓글 생성")
	@PostMapping("/{communityId}")
	public ResponseEntity<CommonResponse.SingleResponse<CommentResponse>> createComment(
		@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable Long communityId,
		@RequestBody CommentRequest commentRequestDTO) {
		return commentService.createComment(customUserPrincipal, communityId, commentRequestDTO);
	}

	@ApiOperation(tags = "1. Comment", value = "댓글 조회")
	@GetMapping("/{communityId}")
	public ResponseEntity<CommonResponse.ListResponse<CommentResponse>> createComment(
			@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable Long communityId) {
		return commentService.getCommentList(customUserPrincipal, communityId);
	}

	@ApiOperation(tags = "1. Comment", value = "댓글 수정")
	@PutMapping("/{commentId}")
	public ResponseEntity<CommonResponse.SingleResponse<CommentResponse>> updateComment(
		@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable Long commentId,
		@RequestBody CommentRequest commentRequestDTO) {
		return commentService.updateComment(customUserPrincipal, commentId, commentRequestDTO);

	}

	@ApiOperation(tags = "1. Comment", value = "댓글 삭제")
	@DeleteMapping("/{commentId}")
	public ResponseEntity<CommonResponse.GeneralResponse> deleteComment(
		@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable Long commentId) {
		return commentService.deleteComment(customUserPrincipal, commentId);
	}
}