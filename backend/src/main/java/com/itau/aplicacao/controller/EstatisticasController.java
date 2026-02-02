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

@RestController
@RequestMapping("/api/v1/estatisticas")
@RequiredArgsConstructor
public class EstatisticasController {

    private static final Logger log = LoggerFactory.getLogger(EstatisticasController.class);

    private final TransacaoService transacaoService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EstatisticasResponse> obterEstatisticas() {
        log.info("Consulta de estatísticas");
        EstatisticasResponse estatisticas = transacaoService.calcularEstatisticas();
        return ResponseEntity.ok(estatisticas);
    }
}
