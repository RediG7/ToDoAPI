package com.guleksiredi.todo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        SwaggerParseResult parseResult = new OpenAPIV3Parser().readLocation("openapi.yml", null, null);
        OpenAPI openAPI = parseResult.getOpenAPI();

        return openAPI;
    }
}
