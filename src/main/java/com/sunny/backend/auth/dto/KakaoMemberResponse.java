package com.sunny.backend.auth.dto;

import static com.sunny.backend.common.ComnConstant.*;

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
public class KakaoMemberResponse {
	Long id;
	KakaoAccount kakaoAccount;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	public static class KakaoAccount {
		Profile profile;
		String email;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	public static class Profile {
		String nickname;
		String profileImageUrl;

		public void checkDefaultImage() {
			if (this.profileImageUrl.equals(KAKAO_DEFAULT_IMAGE)) {
				this.profileImageUrl = SUNNY_DEFAULT_IMAGE;
			}
		}
	}
}
