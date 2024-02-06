package com.sunny.backend.user.dto;

import com.sunny.backend.user.domain.Users;

public record ProfileResponse (
    Long id,
    String name,
    String profile
) {
    public static ProfileResponse from(Users users) {
        return new ProfileResponse(
            users.getId(),
            users.getNickname(),
            users.getProfile()
        );
    }
}
