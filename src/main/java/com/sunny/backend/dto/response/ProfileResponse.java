package com.sunny.backend.dto.response;

import com.sunny.backend.user.domain.Users;

public record ProfileResponse (
    Long id,
    String name,
    String profile
) {
    public static ProfileResponse from(Users users) {
        return new ProfileResponse(
            users.getId(),
            users.getName(),
            users.getProfile()
        );
    }
}
