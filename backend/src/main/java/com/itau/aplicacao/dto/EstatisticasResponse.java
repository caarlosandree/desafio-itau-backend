package com.itau.aplicacao.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstatisticasResponse {

    private long count;
    private BigDecimal sum;
    private BigDecimal avg;
    private BigDecimal min;
    private BigDecimal max;
}
