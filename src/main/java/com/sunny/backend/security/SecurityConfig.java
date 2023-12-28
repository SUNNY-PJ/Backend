package com.sunny.backend.security;

import com.sunny.backend.security.handler.CustomOAuth2AuthenticationSuccessHandler;
import com.sunny.backend.user.repository.CustomAuthorizationRequestRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.sunny.backend.security.handler.CustomOAuth2AuthenticationFailureHandler;

import com.sunny.backend.security.jwt.CustomJwtFilter;
import com.sunny.backend.security.service.CustomOAuth2UserService;
import com.sunny.backend.security.service.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final CustomJwtFilter customJwtFilter;
	private final CustomOAuth2UserService oAuth2UserService;
	private final CustomAuthorizationRequestRepository customAuthorizationRequestRepository;
	private final CustomOAuth2AuthenticationSuccessHandler oAuth2AuthorizationSuccessHandler;
	private final CustomOAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		//Spring Seucrity 로직을 수행하지 않고 아래 요청에 접근
		return (web) -> web.ignoring().
			antMatchers("/images/**", "/js/**", "/webjars/**", "/swagger-ui/**",
				"/swagger-resources/**", "/v3/api-docs", "/favicon**");
	}
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.cors().configurationSource(corsConfigurationSource())
				.and()
				.csrf().disable()

				.headers().frameOptions().sameOrigin()
				.and()
				.httpBasic().disable()
				.formLogin().disable()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

				// 401, 403 Exception 핸들링
				.and()
				.exceptionHandling()
				.authenticationEntryPoint(jwtAuthenticationEntryPoint)
				.accessDeniedHandler(jwtAccessDeniedHandler)

				// HttpServletRequest를 사용하는 요청들에 대한 접근 제한 설정
				.and()
				.authorizeRequests()
				.antMatchers("/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**", "/h2-console/**").permitAll()
				.antMatchers("/auth/**").permitAll()
				// .anyRequest().authenticated()
			.anyRequest().permitAll()

				.and()
				.oauth2Login()
				.authorizationEndpoint()
				.baseUri("/oauth2/authorize")
				.authorizationRequestRepository(customAuthorizationRequestRepository)
				.and()
				.authorizationEndpoint().baseUri("/oauth2/authorize")
				.and()
				.redirectionEndpoint().baseUri("/oauth2/callback/**")
				.and()
				.userInfoEndpoint().userService(oAuth2UserService)
				.and()
				.successHandler(oAuth2AuthorizationSuccessHandler)
				.failureHandler(oAuth2AuthenticationFailureHandler);
		http.addFilterBefore(customJwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.addAllowedOrigin("*");
		configuration.addAllowedHeader("*");
		configuration.addAllowedMethod("*");
		configuration.setAllowCredentials(false);
		configuration.setExposedHeaders(Arrays.asList("Access-Control-Allow-Headers", "Authorization, x-xsrf-token, Access-Control-Allow-Headers, Origin, Accept, X-Requested-With, " +
				"Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers", "Content-Disposition"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}


}
