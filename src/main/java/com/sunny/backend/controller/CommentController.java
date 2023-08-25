package com.sunny.backend.controller;


import com.sunny.backend.config.AuthUser;
import com.sunny.backend.dto.request.CommentRequest;
import com.sunny.backend.service.comment.CommentService;
import com.sunny.backend.user.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{communityId}")
    public ResponseEntity insertComment(@PathVariable Long communityId, @AuthUser Users user,
                                        @RequestBody CommentRequest commentRequestDTO) {
        return commentService.insertComment(communityId,user, commentRequestDTO);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity updateComment(@ApiIgnore @AuthUser Users user, @PathVariable Long commentId, @RequestBody CommentRequest commentRequestDTO) {
        return commentService.updateComment(user, commentId, commentRequestDTO);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity deleteComment(@ApiIgnore @AuthUser Users user, @PathVariable Long commentId) {
        return  commentService.deleteComment(user, commentId);

    }

}