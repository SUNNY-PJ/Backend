package com.sunny.backend.comment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.comment.dto.request.CommentRequest;
import com.sunny.backend.comment.dto.response.CommentResponse;
import com.sunny.backend.comment.service.CommentService;
import com.sunny.backend.common.config.AuthUser;
import com.sunny.backend.common.response.CommonResponse;

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
	@PatchMapping("/{commentId}")
	public ResponseEntity<CommonResponse.SingleResponse<CommentResponse>> updateComment(
		@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable Long commentId,
		@RequestBody CommentRequest commentRequestDTO) {
		return commentService.updateComment(customUserPrincipal, commentId, commentRequestDTO);

	}

	@ApiOperation(tags = "1. Comment", value = "댓글 삭제")
	@DeleteMapping("/{commentId}")
	public ResponseEntity<CommonResponse.SingleResponse<CommentResponse>> deleteComment(
		@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable Long commentId) {
		return commentService.deleteComment(customUserPrincipal, commentId);
	}
}