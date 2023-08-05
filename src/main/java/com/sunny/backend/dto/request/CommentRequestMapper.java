package com.sunny.backend.dto.request;

import com.sunny.backend.entity.Comment;
import com.sunny.backend.repository.comment.CommentRepository;
import com.sunny.backend.user.Users;
import com.sunny.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentRequestMapper {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    public Comment toEntity(CommentRequest commentRequest) {
        if (commentRequest == null) {
            return null;
        }
        Comment comment = new Comment(commentRequest.getContent());

        Users users = userRepository.findById(commentRequest.getUserId()).orElse(null);
        comment.updateWriter(users); //writer 정의에 대해

        Comment parentComment = commentRepository.findById(commentRequest.getParentId()).orElse(null);
        comment.updateParent(parentComment);

        return comment;
    }
}
