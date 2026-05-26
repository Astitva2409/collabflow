package com.astitva.collabflow.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI collabFlowOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CollabFlow API")
                        .description("API documentation for CollabFlow project collaboration platform")
                        .version("v1.0.0"));
    }
}