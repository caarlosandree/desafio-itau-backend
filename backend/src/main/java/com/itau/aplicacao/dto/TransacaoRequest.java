package com.itau.aplicacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Schema(description = "Payload para registro de transação")
public class TransacaoRequest {

    @NotNull(message = "valor é obrigatório")
    @DecimalMin(value = "0", message = "valor deve ser maior ou igual a zero")
    @Schema(description = "Valor da transação (>= 0)", example = "10.50", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal valor;

    @NotNull(message = "dataHora é obrigatória")
    @PastOrPresent(message = "dataHora não pode ser no futuro")
    @Schema(description = "Data e hora da transação (ISO-8601, não pode ser futura)", example = "2025-02-01T12:00:00Z", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime dataHora;
}
