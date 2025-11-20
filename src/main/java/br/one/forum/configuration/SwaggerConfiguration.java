package br.one.forum.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FÓRUM ONE API TEST")
                        .version("1.0")
                        .contact(
                                new Contact()
                                        .email("forum_one@test.mail")
                                        .name("ONE Developers")
                                        .url("https://forum-one.com/contacts")
                        ).description("Sistema de Dinâmico para testes da API Restful do Fórum ONE")
                        .license(new License()
                                .url("https://opensource.org/license/mit")
                                .name("MIT")
                        )
                ).components(
                        new Components().addParameters("pageable", new Parameter()
                                .in("query")
                                .schema(new StringSchema())
                                .name("page, size, sort")
                                .description("Parâmetro de paginação:\n" +
                                        "* page = número da página (default 0)\n" +
                                        "* size = tamanho da página (default 0)\n" +
                                        "* sort = campo, asc | desc"))
                );
    }
}
