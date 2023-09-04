package com.sunny.backend.dto.response;

import com.sunny.backend.entity.Community;
import lombok.Getter;

import java.util.List;

@Getter
//사용자가 스크랩한 게시글 한 번에 볼 때 필요
public class ScrapResponse {
    private String users;
    private List<Community> communityList;



}
