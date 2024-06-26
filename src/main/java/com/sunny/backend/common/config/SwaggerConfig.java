package com.sunny.backend.common.config;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;

import com.fasterxml.classmate.TypeResolver;
import com.sunny.backend.common.MyPageable;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {
	TypeResolver typeResolver = new TypeResolver();

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.OAS_30)
			.ignoredParameterTypes(AuthUser.class)
			.useDefaultResponseMessages(false)
			.securityContexts(Stream.of(securityContext()).collect(Collectors.toList())) // SecurityContext 설정
			.securitySchemes(List.of(this.apiKey())) // ApiKey 설정
			.select()
			.apis(RequestHandlerSelectors.basePackage("com.sunny.backend"))
			.paths(PathSelectors.any())
			.build()
			.apiInfo(apiInfo())
			.alternateTypeRules(AlternateTypeRules.newRule(typeResolver.resolve(Pageable.class),
				typeResolver.resolve(MyPageable.class)));
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
			.title("Swagger")
			.description(" swagger config")
			.version("1.0")
			.build();
	}

	// JWT SecurityContext 구성
	private SecurityContext securityContext() {
		return SecurityContext.builder()
			.securityReferences(defaultAuth())
			// .operationSelector(
			// 	oc -> oc.requestMappingPattern().matches("((?!/api/v1|tibco|login).)*")
			// )
			.build();
	}

	private List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return List.of(new SecurityReference("Authorization", authorizationScopes));
	}

	// ApiKey 정의
	private ApiKey apiKey() {
		return new ApiKey("Authorization", "Authorization", "header");
	}
}
