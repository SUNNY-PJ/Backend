package com.sunny.backend.declaration.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sunny.backend.declaration.domain.CommentDeclaration;

public interface CommentDeclarationRepository extends JpaRepository<CommentDeclaration, Long> {
}
