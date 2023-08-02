package com.sunny.backend.security.handler;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.sunny.backend.security.dto.AuthDto;
import com.sunny.backend.security.jwt.TokenProvider;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	@Value("#{'${app.oauth2.authorizedRedirectUris}'.split(',')}")
	private List<String> authorizedRedirectUris;
	private final TokenProvider tokenProvider;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {
		String targetUrl = determineTargetUrl(request, response, authentication);
		if (response.isCommitted()) {
			logger.debug("response has already been committed. unable to redirect to " + targetUrl);
			return;
		}
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}

	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) {
		// Optional<String> redirectUri = CustomCookie.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
		// 	.map(Cookie::getValue);
		//
		// if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get()))
		// 	throw new BadRequestException("unauthorized Redirect URI");

		String targetUri = "redirect_uri";
		AuthDto.TokenDto tokenDto = tokenProvider.createTokenOAuth2(authentication);
		return UriComponentsBuilder.fromUriString(targetUri)
			.queryParam("token", tokenDto.getAccessToken())
			.build().toUriString();
	}

	private boolean isAuthorizedRedirectUri(String uri) {
		URI clientRedirectUri = URI.create(uri);
		return authorizedRedirectUris
			.stream()
			.anyMatch(authorizedRedirectUri -> {
				URI authorizedURI = URI.create(authorizedRedirectUri);
				if (authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
					&& authorizedURI.getPort() == clientRedirectUri.getPort()) {
					return true;
				}
				return false;
			});
	}
}
