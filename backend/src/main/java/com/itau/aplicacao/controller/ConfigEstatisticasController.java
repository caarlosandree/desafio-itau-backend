package com.itau.aplicacao.controller;

import com.itau.aplicacao.config.JanelaEstatisticasConfig;
import com.itau.aplicacao.dto.JanelaConfigRequest;
import com.itau.aplicacao.dto.JanelaConfigResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/config/estatisticas")
@RequiredArgsConstructor
@Tag(name = "Configuração", description = "Configuração da janela de estatísticas (em segundos)")
public class ConfigEstatisticasController {

    private final JanelaEstatisticasConfig janelaEstatisticasConfig;

    @GetMapping(value = "/janela", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obter janela atual", description = "Retorna a janela em segundos usada no cálculo das estatísticas.")
    public ResponseEntity<JanelaConfigResponse> obterJanela() {
        int segundos = janelaEstatisticasConfig.getJanelaSegundos();
        return ResponseEntity.ok(new JanelaConfigResponse(segundos));
    }

    @PatchMapping(value = "/janela", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Alterar janela", description = "Altera a janela em segundos (1 a 86400). Efeito imediato nas próximas consultas de estatísticas.")
    public ResponseEntity<JanelaConfigResponse> alterarJanela(@Valid @RequestBody JanelaConfigRequest request) {
        janelaEstatisticasConfig.setJanelaSegundos(request.getJanelaSegundos());
        return ResponseEntity.ok(new JanelaConfigResponse(janelaEstatisticasConfig.getJanelaSegundos()));
    }
}
