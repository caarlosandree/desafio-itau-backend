package com.itau.aplicacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Janela de estatísticas atual (em segundos)")
public class JanelaConfigResponse {

    @Schema(description = "Janela em segundos usada no cálculo das estatísticas")
    private int janelaSegundos;
}
