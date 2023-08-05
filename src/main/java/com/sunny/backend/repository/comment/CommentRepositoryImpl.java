package com.sunny.backend.repository.comment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sunny.backend.dto.response.CommentResponse;
import com.sunny.backend.entity.Comment;
import com.sunny.backend.entity.Community;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.*;

import static com.sunny.backend.dto.response.CommentResponse.convertCommentToDto;
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
                .leftJoin(comment.parent).fetchJoin() // leftjoin,fetchjoin 에 대해
                .where(comment.id.eq(id))
                .fetchOne(); // 정확한 의미

        return Optional.ofNullable(selectedComment);
    }

    @Override
    public List<CommentResponse> findByCommunityId(Long id) {

        List<Comment> comments = queryFactory.selectFrom(comment)
                .leftJoin(comment.parent).fetchJoin()
                .where(comment.community.id.eq(id))
                .orderBy(comment.parent.id.asc().nullsFirst(),
                        comment.createdDate.asc())
                .fetch();

        List<CommentResponse> commentResponseDTOList = new ArrayList<>();
        Map<Long, CommentResponse> commentDTOHashMap = new HashMap<>();

        comments.forEach(c -> {
            CommentResponse commentResponseDTO = convertCommentToDto(c);
            commentDTOHashMap.put(commentResponseDTO.getId(), commentResponseDTO);
            if (c.getParent() != null) commentDTOHashMap.get(c.getParent().getId()).getChildren().add(commentResponseDTO);
            else commentResponseDTOList.add(commentResponseDTO);
        });
        return commentResponseDTOList;
    }
}
