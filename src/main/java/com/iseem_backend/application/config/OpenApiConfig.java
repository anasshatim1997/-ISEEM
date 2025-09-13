package com.iseem_backend.application.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "ISEEM Backend API",
                version = "1.0.0",
                description = "REST API for managing users, students, teachers, in ISEEM School .",
                contact = @Contact(
                        name = "ISEEM Support",
                        email = "contact@ecoleiseem.com",
                        url = "https://ecoleiseem.com/"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local development server")
        }
)
public class OpenApiConfig {
}
