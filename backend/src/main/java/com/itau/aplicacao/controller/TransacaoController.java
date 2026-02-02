package com.itau.aplicacao.controller;

import com.itau.aplicacao.dto.TransacaoRequest;
import com.itau.aplicacao.service.TransacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/transacoes")
@RequiredArgsConstructor
@Tag(name = "Transações", description = "Registro e limpeza de transações")
public class TransacaoController {

    private static final Logger log = LoggerFactory.getLogger(TransacaoController.class);

    private final TransacaoService transacaoService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Registrar transação", description = "Registra uma transação (valor e dataHora). Considerada na janela dos últimos 60 segundos para estatísticas.")
    @ApiResponse(responseCode = "201", description = "Transação registrada")
    @ApiResponse(responseCode = "422", description = "Validação falhou (valor ou dataHora inválidos)")
    @ApiResponse(responseCode = "400", description = "Corpo da requisição inválido")
    public ResponseEntity<Void> registrar(@Valid @RequestBody TransacaoRequest request) {
        log.info("Transação recebida");
        transacaoService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    @Operation(summary = "Limpar transações", description = "Remove todas as transações armazenadas em memória.")
    @ApiResponse(responseCode = "200", description = "Transações removidas")
    public ResponseEntity<Void> limparTransacoes() {
        log.info("Limpeza de transações solicitada");
        transacaoService.limparTransacoes();
        return ResponseEntity.ok().build();
    }
}
