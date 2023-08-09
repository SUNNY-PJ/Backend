package com.sunny.backend.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {

	@GetMapping("/token")
	public ResponseEntity kakaoCallback(@RequestParam String Authorization){
		System.out.println(Authorization);


		return new ResponseEntity(new ResponseEntity<>("성공적으로 카카오 로그인 API 코드를 불러왔습니다.", HttpStatus.OK), HttpStatus.OK);
	}

	//
    @GetMapping("/error")
    public ResponseEntity errorPage(@RequestParam String error) {
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
}
