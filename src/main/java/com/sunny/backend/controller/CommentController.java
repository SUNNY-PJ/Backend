package com.sunny.backend.controller;


import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.config.AuthUser;
import com.sunny.backend.dto.request.comment.CommentRequest;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import com.sunny.backend.service.comment.CommentService;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Comment", description = "댓글 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    //댓글 등록

    @ApiOperation(tags = "댓글", value = "댓글 생성")
    @PostMapping("/{communityId}")
    public ResponseEntity<CommonResponse.SingleResponse> createComment(@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable Long communityId,
                                                        @RequestBody CommentRequest commentRequestDTO) {
        return ResponseEntity.ok().body(commentService.createComment(customUserPrincipal,communityId, commentRequestDTO));

    }
    //댓글 수정
    @ApiOperation(tags = "댓글", value = "댓글 수정")
    @PutMapping("/{commentId}")
    public ResponseEntity<CommonResponse.SingleResponse> updateComment(@AuthUser CustomUserPrincipal customUserPrincipal,@PathVariable Long commentId, @RequestBody CommentRequest commentRequestDTO) {
        return ResponseEntity.ok().body(commentService.updateComment(customUserPrincipal, commentId, commentRequestDTO));

    }


    //댓글 삭제
    @ApiOperation(tags = "댓글", value = "댓글 삭제")
    @DeleteMapping("/{commentId}")

    public ResponseEntity<CommonResponse.GeneralResponse> deleteComment(@AuthUser CustomUserPrincipal customUserPrincipal, @PathVariable Long commentId) {
        return ResponseEntity.ok().body(commentService.deleteComment(customUserPrincipal, commentId));

    }

}