package com.itau.aplicacao.controller;

import com.itau.aplicacao.model.Transacao;
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

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DisplayName("EstatisticasController (integração)")
class EstatisticasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransacaoStore transacaoStore;

    @BeforeEach
    void setUp() {
        transacaoStore.limparTodas();
    }

    @Nested
    @DisplayName("GET /api/v1/estatisticas")
    class GetEstatisticas {

        @Test
        void deveRetornar200EJsonComContratoExato() throws Exception {
            mockMvc.perform(get("/api/v1/estatisticas")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.count").exists())
                    .andExpect(jsonPath("$.sum").exists())
                    .andExpect(jsonPath("$.avg").exists())
                    .andExpect(jsonPath("$.min").exists())
                    .andExpect(jsonPath("$.max").exists());
        }

        @Test
        void deveRetornarEstatisticasZeradasQuandoNaoHaTransacoes() throws Exception {
            mockMvc.perform(get("/api/v1/estatisticas")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.count").value(0))
                    .andExpect(jsonPath("$.sum").value(0))
                    .andExpect(jsonPath("$.avg").value(0))
                    .andExpect(jsonPath("$.min").value(0))
                    .andExpect(jsonPath("$.max").value(0));
        }

        @Test
        void deveRetornarEstatisticasCalculadasQuandoHaTransacoes() throws Exception {
            OffsetDateTime agora = OffsetDateTime.now(ZoneOffset.UTC);
            transacaoStore.adicionar(new Transacao(BigDecimal.valueOf(10), agora));
            transacaoStore.adicionar(new Transacao(BigDecimal.valueOf(20), agora));

            mockMvc.perform(get("/api/v1/estatisticas")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.count").value(2))
                    .andExpect(jsonPath("$.sum").value(30))
                    .andExpect(jsonPath("$.avg").value(15))
                    .andExpect(jsonPath("$.min").value(10))
                    .andExpect(jsonPath("$.max").value(20));
        }
    }
}
