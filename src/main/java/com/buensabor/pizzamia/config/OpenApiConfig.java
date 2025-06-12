package com.buensabor.pizzamia.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API PizzaMia")
                        .description("Documentación de la API para el sistema de gestión de PizzaMia")
                        .version("1.0")
                        .contact(new Contact()
                                .name("PizzaMia")
                                .email("contacto@pizzamia.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor de desarrollo")
                ));
    }
}