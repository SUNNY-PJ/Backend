package com.sunny.backend.comment.dto.request;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.sunny.backend.comment.domain.Comment;
import com.sunny.backend.common.GenericMapper;

@Mapper(componentModel = "spring")
public interface CommentRequestMapper extends GenericMapper<CommentRequest, Comment> {
	CommentRequestMapper INSTANCE = Mappers.getMapper(CommentRequestMapper.class);
}