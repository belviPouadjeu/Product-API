package com.belvinard.products_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                    .title("🛒 Products Inventory API")
                    .version("1.0.0")
                    .description("API de gestion d'un inventaire de produits avec suivi des " +
                            "stocks, alertes, et documentation Swagger.")
                    .contact(new Contact()
                        .name("Belvinard Dev")
                        .email("belvinard@example.com")
                        .url("https://github.com/belvinard"))
                    .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"))
                );
    }
}
