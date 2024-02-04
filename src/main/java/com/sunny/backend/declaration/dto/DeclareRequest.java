package com.sunny.backend.declaration.dto;

import com.sunny.backend.declaration.domain.DeclarationStatus;

public record DeclareRequest (
	Long id,
	DeclarationStatus status,
	String reason
){
}
