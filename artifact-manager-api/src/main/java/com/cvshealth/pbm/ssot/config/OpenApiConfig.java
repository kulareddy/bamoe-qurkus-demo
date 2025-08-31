package com.cvshealth.pbm.ssot.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI artifactManagerOpenAPI() {
        Contact contact = new Contact()
                .email("support@example.com")
                .name("Artifact Manager Support")
                .url("https://www.example.com");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Artifact Manager API")
                .version("1.0")
                .contact(contact)
                .description("This API provides endpoints for managing artifacts with RESTful services.")
                .termsOfService("https://www.example.com/terms")
                .license(mitLicense);

        // Define OAuth2 security scheme
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Enter JWT Bearer token");

        return new OpenAPI()
                .info(info)
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Development server"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", bearerAuth))
                .addSecurityItem(new SecurityRequirement()
                        .addList("bearerAuth"));
    }
}
