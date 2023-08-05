
package com.sunny.backend.security.handler;

import com.sunny.backend.security.dto.AuthDto;
import com.sunny.backend.security.exception.BadRequestException;
import com.sunny.backend.security.jwt.TokenProvider;
import com.sunny.backend.user.CustomCookie;
import com.sunny.backend.user.repository.CustomAuthorizationRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.sunny.backend.user.repository.CustomAuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;


@RequiredArgsConstructor
@Component
public class CustomOAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final TokenProvider tokenProvider;

	@Value("#{'${app.oauth2.authorizedRedirectUris}'.split(',')}")
	private List<String> authorizedRedirectUris;

	private final CustomAuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		String targetUrl = determineTargetUrl(request, response, authentication);

		if (response.isCommitted()) {
			logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
			return;
		}

		clearAuthenticationAttributes(request, response);
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}

	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		//쿠키 추가
		Optional<String> redirectUri = CustomCookie.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
				.map(Cookie::getValue);

		if(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
			throw new BadRequestException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
		}

		String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

		AuthDto.TokenDto Token = tokenProvider.createTokenOAuth2(authentication);
		return UriComponentsBuilder.fromUriString(targetUrl).queryParam("Authorization", Token.getAccessToken()).build().toUriString();
	}

	protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
		super.clearAuthenticationAttributes(request);
		httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
	}

	private boolean isAuthorizedRedirectUri(String uri) {
		URI clientRedirectUri = URI.create(uri);
		return authorizedRedirectUris
				.stream()
				.anyMatch(authorizedRedirectUri -> {
					// Only validate host and port. Let the clients use different paths if they want to
					URI authorizedURI = URI.create(authorizedRedirectUri);
					if(authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
							&& authorizedURI.getPort() == clientRedirectUri.getPort()) {
						return true;
					}
					return false;
				});
	}
}

