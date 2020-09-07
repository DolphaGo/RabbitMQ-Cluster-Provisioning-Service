package com.navercorp.RabbitMQMonitoring.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {
    private ApiInfo metadata() {
        return new ApiInfoBuilder().title("RabbitMQ-Cluster Management").description("Naver Business Platform Corp.")
                .version("2.0").build();
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select().apis(RequestHandlerSelectors.basePackage("com.navercorp.rabbit.controller"))
                .paths(PathSelectors.any()).build().apiInfo(metadata());
    }
}