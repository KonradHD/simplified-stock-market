package com.stockmarket.stockmarket_core.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        contact = @Contact(
            name = "Konrad Ćwięka",
            email = "konrad4cwieka@gmail.com"
        ),
        description = "OpenApi documentation for Simplified Stock Market.",
        title = "Simplified Stock Market API",
        version = "1.0",
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(url = "/", description = "Default Server")
    }
)
public class OpenAPIConfig {
    
}
