package com.sunny.backend.dto.request.community;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.sunny.backend.community.domain.BoardType;
import lombok.Getter;

@Getter
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CommunityRequest {

    private String title;
    private String contents;
    private BoardType type;

}
