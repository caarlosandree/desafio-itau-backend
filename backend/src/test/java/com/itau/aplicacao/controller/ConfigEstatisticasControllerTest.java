package com.itau.aplicacao.controller;

import com.itau.aplicacao.Application;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itau.aplicacao.config.JanelaEstatisticasConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DisplayName("ConfigEstatisticasController (integração)")
class ConfigEstatisticasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JanelaEstatisticasConfig janelaEstatisticasConfig;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        janelaEstatisticasConfig.setJanelaSegundos(60);
    }

    @Nested
    @DisplayName("GET /api/v1/config/estatisticas/janela")
    class GetJanela {

        @Test
        void deveRetornarJanelaAtual() throws Exception {
            janelaEstatisticasConfig.setJanelaSegundos(60);

            mockMvc.perform(get("/api/v1/config/estatisticas/janela")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.janelaSegundos").value(60));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/config/estatisticas/janela")
    class PatchJanela {

        @Test
        void deveAlterarJanelaERetornarNovoValor() throws Exception {
            String body = objectMapper.writeValueAsString(Map.of("janelaSegundos", 120));

            mockMvc.perform(patch("/api/v1/config/estatisticas/janela")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.janelaSegundos").value(120));

            mockMvc.perform(get("/api/v1/config/estatisticas/janela")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.janelaSegundos").value(120));
        }

        @Test
        void deveRetornar422QuandoJanelaMenorQue1() throws Exception {
            String body = objectMapper.writeValueAsString(Map.of("janelaSegundos", 0));

            mockMvc.perform(patch("/api/v1/config/estatisticas/janela")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().is(422));
        }

        @Test
        void deveRetornar422QuandoJanelaMaiorQue86400() throws Exception {
            String body = objectMapper.writeValueAsString(Map.of("janelaSegundos", 86401));

            mockMvc.perform(patch("/api/v1/config/estatisticas/janela")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().is(422));
        }
    }
}
