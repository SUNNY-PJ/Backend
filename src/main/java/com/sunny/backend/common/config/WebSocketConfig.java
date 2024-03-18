package com.sunny.backend.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.sunny.backend.common.StompHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker // 웹 소켓을 사용함
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	private final StompHandler stompHandler;

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// 해당 경로로 들어오는것을 구독하는것으로 정한다.
		registry.enableSimpleBroker("/sub");

		// @MessageMapping("hello") 라면 경로는 -> /pub/hello
		registry.setApplicationDestinationPrefixes("/pub");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/stomp") // ex ) ws://localhost:8080/stomp
			.setAllowedOriginPatterns("*") // 허용하는 도메인 주소 (일단 모두 허용으로 바꾸었다.)
		// .withSockJS(); // 웹 소켓 사용
		;
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(stompHandler);
	}

}