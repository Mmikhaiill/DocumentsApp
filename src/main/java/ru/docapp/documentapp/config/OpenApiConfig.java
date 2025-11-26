package ru.docapp.documentapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI documentAppOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Document Management API")
                        .description("Master-Detail система управления документами и спецификациями")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("DocumentApp Team")
                                .email("support@docapp.local"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))
                );
    }
}