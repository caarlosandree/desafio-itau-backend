package com.itau.aplicacao.controller;

import com.itau.aplicacao.dto.EstatisticasResponse;
import com.itau.aplicacao.service.TransacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/estatisticas")
@RequiredArgsConstructor
@Tag(name = "Estatísticas", description = "Estatísticas das transações na janela dos últimos 60 segundos")
public class EstatisticasController {

    private static final Logger log = LoggerFactory.getLogger(EstatisticasController.class);

    private final TransacaoService transacaoService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obter estatísticas", description = "Retorna count, sum, avg, min e max das transações dos últimos 60 segundos.")
    public ResponseEntity<EstatisticasResponse> obterEstatisticas() {
        log.info("Consulta de estatísticas");
        EstatisticasResponse estatisticas = transacaoService.calcularEstatisticas();
        return ResponseEntity.ok(estatisticas);
    }
}
