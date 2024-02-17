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

    @Transactional
    public void nullifyUserAssociation(Long userId, EntityManager entityManager) {
        // Nullify the association between Comment and Users
        entityManager.createQuery("UPDATE Comment c SET c.users = null WHERE c.users.id = :userId")
            .setParameter("userId", userId)
            .executeUpdate();

        // Delete the associated child entities (e.g., CommentNotification) only for non-null userId
        if (userId != null) {
            entityManager.createQuery("DELETE FROM CommentNotification cn WHERE cn.comment.users.id = :userId")
                .setParameter("userId", userId)
                .executeUpdate();
        } else {
            // If userId is null, nullify the association in CommentNotification without filtering by userId
            entityManager.createQuery("UPDATE CommentNotification cn SET cn.comment = null WHERE cn.comment.users IS NULL")
                .executeUpdate();
        }
    }

//    @Override
//    public List<CommentResponse> findByCommunityId(Long id) {
//        List<Comment> comments = queryFactory.selectFrom(comment)
//                .leftJoin(comment.parent).fetchJoin()
//                .where(comment.community.id.eq(id))
//                .orderBy(comment.parent.id.asc().nullsFirst(),
//                        comment.createdDate.asc())
//                .fetch();
//        List<CommentResponse> commentResponseDTOList = new ArrayList<>();
//        Map<Long, CommentResponse> commentDTOHashMap = new HashMap<>();
//        comments.forEach(c -> {
//            CommentResponse commentResponseDTO = convertCommentToDto(c);
//            commentDTOHashMap.put(commentResponseDTO.getId(), commentResponseDTO);
//            if (c.getParent() != null) {
//                commentDTOHashMap.get(c.getParent().getId()).getChildren().add(commentResponseDTO);
//            }
//            else {
//                commentResponseDTOList.add(commentResponseDTO);
//            }
//        });
//        return commentResponseDTOList;
//    }
}
