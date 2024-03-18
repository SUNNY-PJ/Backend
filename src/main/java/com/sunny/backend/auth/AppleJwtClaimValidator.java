package com.sunny.backend.auth;

import io.jsonwebtoken.Claims;

public interface AppleJwtClaimValidator {
  void validateClaims(Claims claims);
}