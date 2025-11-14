package br.one.forum.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
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
                );
    }
}
