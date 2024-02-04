package com.sunny.backend.declaration.dto;

import com.sunny.backend.declaration.domain.DeclarationStatus;

public record DeclareStatusRequest(
	Long id,
	DeclarationStatus status
) {
}
