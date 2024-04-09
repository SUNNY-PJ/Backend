package com.sunny.backend.auth.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoRequest {

	Long id;
	String email;
	//  KakaoAccount kakaoAccount;
	String nickname;
	String profile;
}

//  @Getter
//  @Setter
//  @NoArgsConstructor
//  @AllArgsConstructor
//  @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
//  public static class KakaoAccount {
//    Profile profile;
//    String email;
//  }
//
//  @Getter
//  @Setter
//  @NoArgsConstructor
//  @AllArgsConstructor
//  @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
//  public static class Profile {
//    String nickname;
//    String profileImageUrl;
//
//    public void checkDefaultImage() {
//      if (this.profileImageUrl.equals(KAKAO_DEFAULT_IMAGE)) {
//        this.profileImageUrl = SUNNY_DEFAULT_IMAGE;
//      }
//    }
//  }
