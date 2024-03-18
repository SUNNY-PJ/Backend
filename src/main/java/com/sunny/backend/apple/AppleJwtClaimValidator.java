package com.sunny.backend.apple;

import io.jsonwebtoken.Claims;

public interface AppleJwtClaimValidator {
  void validateClaims(Claims claims);
}