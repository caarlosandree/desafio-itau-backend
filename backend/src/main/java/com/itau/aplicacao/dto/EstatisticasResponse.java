package com.itau.aplicacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Estatísticas das transações na janela de 60 segundos")
public class EstatisticasResponse {

    @Schema(description = "Quantidade de transações")
    private long count;

    @Schema(description = "Soma dos valores")
    private BigDecimal sum;

    @Schema(description = "Média dos valores")
    private BigDecimal avg;

    @Schema(description = "Menor valor")
    private BigDecimal min;

    @Schema(description = "Maior valor")
    private BigDecimal max;
}
