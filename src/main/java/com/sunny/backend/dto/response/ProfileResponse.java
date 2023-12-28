package com.sunny.backend.dto.response;

import com.sunny.backend.entity.Consumption;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class ProfileResponse {
    private Long id;
    private String nickname;
    private String profile;

    public ProfileResponse(Long id,String nickname, String profile) {
        this.id=id;
        this.nickname = nickname;
        this.profile = profile;
    }

}
