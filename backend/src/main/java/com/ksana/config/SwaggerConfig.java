package com.ksana.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI ksanaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("KSANA API")
                        .description("KSANA Backend APIs")
                        .version("v1.0"));
    }
}