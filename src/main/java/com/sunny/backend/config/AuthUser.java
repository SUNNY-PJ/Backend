package com.sunny.backend.config;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) //  설정한 부분까지 정책이 유지되어야 한다.
@Target(ElementType.PARAMETER) // 커스텀한 어노테이션이 사용되는 위치를 설정한다. / 파라미터
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : #this") // AnonymousAuthenticationFilter에 의해 생성된 Authentication의 경우 null을 반환.
public @interface AuthUser {
}
