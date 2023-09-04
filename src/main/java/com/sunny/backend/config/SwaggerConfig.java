package com.sunny.backend.config;

import com.fasterxml.classmate.TypeResolver;
import com.sunny.backend.common.MyPageable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.awt.print.Pageable;

@Configuration
public class SwaggerConfig {
    TypeResolver typeResolver = new TypeResolver();

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .ignoredParameterTypes(AuthUser.class)
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.omo.be"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .alternateTypeRules(AlternateTypeRules.newRule(typeResolver.resolve(Pageable.class),
                        typeResolver.resolve(MyPageable.class)));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(" Swagger")
                .description(" swagger config")
                .version("1.0")
                .build();
    }

}
