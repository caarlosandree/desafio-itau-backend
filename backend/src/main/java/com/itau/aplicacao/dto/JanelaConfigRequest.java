package com.itau.aplicacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Schema(description = "Payload para alterar a janela de estatísticas (em segundos)")
public class JanelaConfigRequest {

    @Min(value = 1, message = "janelaSegundos deve ser no mínimo 1")
    @Max(value = 86400, message = "janelaSegundos deve ser no máximo 86400 (24 horas)")
    @Schema(description = "Janela em segundos para cálculo das estatísticas (1 a 86400)", example = "120", requiredMode = Schema.RequiredMode.REQUIRED)
    private int janelaSegundos;
}
