package com.sunny.backend.user;

import com.sunny.backend.common.CommonResponse;
import com.sunny.backend.common.ResponseService;
import com.sunny.backend.service.KaKaoService;
import com.sunny.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final KaKaoService kaKaoService;
    private final UserRepository userRepository;
    private final ResponseService responseService;
    public CommonResponse.GeneralResponse kakaoLogin(String access_Token) throws IOException {
        Map<String, Object> userInfo = kaKaoService.getUserInfo(access_Token);
        System.out.println(userInfo);

        Users users = Users.builder()
                .email(userInfo.get("email").toString())
                .name(userInfo.get("nickname").toString())
                .build();
        userRepository.save(users);

        return responseService.getGeneralResponse(HttpStatus.OK.value(), "success");

    }


}
