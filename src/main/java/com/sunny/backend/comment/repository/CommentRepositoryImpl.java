package com.sunny.backend.comment.repository;

import static com.sunny.backend.comment.domain.QComment.comment;
import static com.sunny.backend.comment.dto.response.CommentResponse.convertCommentToDto;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sunny.backend.comment.dto.response.CommentResponse;
import com.sunny.backend.comment.domain.Comment;
import javax.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.*;
import org.springframework.transaction.annotation.Transactional;

public class CommentRepositoryImpl extends QuerydslRepositorySupport implements CommentCustomRepository{
    private JPAQueryFactory queryFactory;
    private final EntityManager entityManager;


    public CommentRepositoryImpl(JPAQueryFactory jpaQueryFactory,EntityManager entityManager) {
        super(Comment.class);
        this.queryFactory = jpaQueryFactory;
        this.entityManager = entityManager;
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



}
