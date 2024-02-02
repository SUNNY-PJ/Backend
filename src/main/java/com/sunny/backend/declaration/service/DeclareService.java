package com.sunny.backend.declaration.service;

import org.springframework.stereotype.Service;

import com.sunny.backend.declaration.repository.CommentDeclarationRepository;
import com.sunny.backend.declaration.repository.CommunityDeclarationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeclareService {
	private final CommentDeclarationRepository commentDeclarationRepository;
	private final CommunityDeclarationRepository communityDeclarationRepository;


}
