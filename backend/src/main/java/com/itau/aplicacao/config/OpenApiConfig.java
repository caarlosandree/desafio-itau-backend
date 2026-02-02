package com.itau.aplicacao.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Transações e Estatísticas")
                        .description("API REST para registro de transações e consulta de estatísticas (janela de 60 segundos).")
                        .version("1.0"));
    }
}
