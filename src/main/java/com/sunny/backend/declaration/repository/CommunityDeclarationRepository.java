package com.sunny.backend.declaration.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sunny.backend.declaration.domain.CommunityDeclaration;

public interface CommunityDeclarationRepository extends JpaRepository<CommunityDeclaration, Long> {
}
