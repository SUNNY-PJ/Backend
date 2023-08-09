package com.sunny.backend.controller;


import com.sunny.backend.config.AuthUser;
import com.sunny.backend.dto.request.CommentRequest;
import com.sunny.backend.service.CommentService;
import com.sunny.backend.user.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comment/{contestId}")
    public ResponseEntity<?> insert(@PathVariable Long contestId,@AuthUser Users user,
                                    @RequestBody CommentRequest commentRequestDTO) {
        return commentService.insert(contestId,user, commentRequestDTO);

    }

    @PutMapping("/comment/{commentId}")
    public ResponseEntity<?> update(@AuthUser Users user,@PathVariable Long commentId, @RequestBody CommentRequest commentRequestDTO) {
        return commentService.update(user, commentId, commentRequestDTO);

    }

    @DeleteMapping("/comment/{commentId}")

    public ResponseEntity<?> delete(@AuthUser Users user, @PathVariable Long commentId) {
        return  commentService.delete(user, commentId);

    }

}