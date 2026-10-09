package com.edabip.users.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI edabipOpenApi() {
        return new OpenAPI().info(new Info()
                .title("EDABIP - Users: User Management API")
                .description("Enterprise Data Analytics & Business Intelligence Platform - "
                        + "REST API to list, search, add, edit and delete users and fetch user stats.")
                .version("v1.0.0")
                .contact(new Contact().name("Dheepanraj")));
    }
}
