package com.sunny.backend.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sunny.backend.dto.response.comment.CommentResponse;
import com.sunny.backend.comment.domain.Comment;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.*;

import static com.sunny.backend.dto.response.comment.CommentResponse.convertCommentToDto;
import static com.sunny.backend.entity.QComment.comment;

public class CommentRepositoryImpl extends QuerydslRepositorySupport implements CommentCustomRepository{
    private JPAQueryFactory queryFactory;

    public CommentRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        super(Comment.class);
        this.queryFactory = jpaQueryFactory;
    }

    @Override
    public Optional<Comment> findCommentByIdWithParent(Long id) {
        Comment selectedComment=queryFactory.select(comment)
                .from(comment)
                .leftJoin(comment.parent).fetchJoin()
                .where(comment.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(selectedComment);
    }

    @Override
    public List<CommentResponse> findByCommunityId(Long id) {
        List<Comment> comments = queryFactory.selectFrom(comment)
                .leftJoin(comment.parent).fetchJoin()
                .where(comment.community.id.eq(id))
                .orderBy(comment.parent.id.asc().nullsFirst(), //상위 댓글이 하위 댓글 앞에 오도록
                        comment.createdDate.asc())
                .fetch();

        List<CommentResponse> commentResponseDTOList = new ArrayList<>();
        Map<Long, CommentResponse> commentDTOHashMap = new HashMap<>();

        comments.forEach(c -> {
            CommentResponse commentResponseDTO = convertCommentToDto(c);
            commentDTOHashMap.put(commentResponseDTO.getId(), commentResponseDTO);
            if (c.getParent() != null) {
                commentDTOHashMap.get(c.getParent().getId()).getChildren().add(commentResponseDTO);
            }
            else {
                commentResponseDTOList.add(commentResponseDTO);
            }
        });
        return commentResponseDTOList;
    }
}
