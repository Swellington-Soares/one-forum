package br.one.forum.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    @Value("${api.base-url}")
    private String apiBaseUrl;

    @Value("${api.front-url}")
    private String frontendUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Forum ONE API")
                        .description("API REST para aplicação de fórum colaborativo com autenticação JWT, " +
                                "gerenciamento de tópicos, comentários, categorias e upload de imagens.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Forum ONE Team")
                                .email("contato@forumone.com")
                                .url(frontendUrl))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url(apiBaseUrl)
                                .description("Servidor de Produção"),
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor Local")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT obtido através do endpoint /auth/login")));
    }
}
