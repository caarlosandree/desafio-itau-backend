package com.itau.aplicacao.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class TransacaoRequest {

    @NotNull(message = "valor é obrigatório")
    @DecimalMin(value = "0", message = "valor deve ser maior ou igual a zero")
    private BigDecimal valor;

    @NotNull(message = "dataHora é obrigatória")
    @PastOrPresent(message = "dataHora não pode ser no futuro")
    private OffsetDateTime dataHora;
}
