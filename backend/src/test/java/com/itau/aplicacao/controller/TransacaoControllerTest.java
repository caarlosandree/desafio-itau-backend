package com.itau.aplicacao.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itau.aplicacao.store.TransacaoStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.itau.aplicacao.Application;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DisplayName("TransacaoController (integração)")
class TransacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    private TransacaoStore transacaoStore;

    @BeforeEach
    void setUp() {
        transacaoStore.limparTodas();
    }

    @Nested
    @DisplayName("POST /api/v1/transacoes")
    class PostTransacoes {

        @Test
        void deveAceitarTransacaoValidaERetornar201() throws Exception {
            String dataHora = OffsetDateTime.now(ZoneOffset.UTC).toString();
            String body = objectMapper.writeValueAsString(Map.of(
                    "valor", "10.00",
                    "dataHora", dataHora
            ));

            mockMvc.perform(post("/api/v1/transacoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated());
        }

        @Test
        void deveAceitarValorZero() throws Exception {
            String dataHora = OffsetDateTime.now(ZoneOffset.UTC).toString();
            String body = objectMapper.writeValueAsString(Map.of(
                    "valor", "0",
                    "dataHora", dataHora
            ));

            mockMvc.perform(post("/api/v1/transacoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated());
        }

        @Test
        void deveRetornar422QuandoValorAusente() throws Exception {
            String dataHora = OffsetDateTime.now(ZoneOffset.UTC).toString();
            String body = objectMapper.writeValueAsString(Map.of("dataHora", dataHora));

            mockMvc.perform(post("/api/v1/transacoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().is(422));
        }

        @Test
        void deveRetornar422QuandoDataHoraAusente() throws Exception {
            String body = objectMapper.writeValueAsString(Map.of("valor", "10.00"));

            mockMvc.perform(post("/api/v1/transacoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().is(422));
        }

        @Test
        void deveRetornar422QuandoValorNegativo() throws Exception {
            String dataHora = OffsetDateTime.now(ZoneOffset.UTC).toString();
            String body = objectMapper.writeValueAsString(Map.of(
                    "valor", "-1.00",
                    "dataHora", dataHora
            ));

            mockMvc.perform(post("/api/v1/transacoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().is(422));
        }

        @Test
        void deveRetornar422QuandoDataHoraNoFuturo() throws Exception {
            String dataFutura = OffsetDateTime.now(ZoneOffset.UTC).plusDays(1).toString();
            String body = objectMapper.writeValueAsString(Map.of(
                    "valor", "10.00",
                    "dataHora", dataFutura
            ));

            mockMvc.perform(post("/api/v1/transacoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().is(422));
        }

        @Test
        void deveRetornar400QuandoCorpoNaoEhJson() throws Exception {
            mockMvc.perform(post("/api/v1/transacoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("texto invalido"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void deveRetornar400QuandoContentTypeNaoEhJson() throws Exception {
            String body = "{\"valor\":\"10.00\",\"dataHora\":\"" + OffsetDateTime.now(ZoneOffset.UTC) + "\"}";

            mockMvc.perform(post("/api/v1/transacoes")
                            .contentType(MediaType.TEXT_PLAIN)
                            .content(body))
                    .andExpect(status().isUnsupportedMediaType());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/transacoes")
    class DeleteTransacoes {

        @Test
        void deveLimparTransacoesERetornar200() throws Exception {
            mockMvc.perform(delete("/api/v1/transacoes"))
                    .andExpect(status().isOk());
        }
    }
}
