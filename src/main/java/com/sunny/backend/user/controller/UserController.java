package com.sunny.backend.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {


	@GetMapping("/token")
	public ResponseEntity kakaoCallback(@RequestParam String Authorization){
		System.out.println(Authorization);
		return new ResponseEntity(HttpStatus.OK);
	}
}
