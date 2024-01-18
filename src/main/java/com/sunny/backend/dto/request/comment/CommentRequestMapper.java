package com.sunny.backend.dto.request.comment;


import com.sunny.backend.common.GenericMapper;
import com.sunny.backend.comment.domain.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CommentRequestMapper extends GenericMapper<CommentRequest, Comment> {
    CommentRequestMapper INSTANCE = Mappers.getMapper(CommentRequestMapper.class);
}