package com.itau.aplicacao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class Transacao {

    private final BigDecimal valor;
    private final OffsetDateTime dataHora;
}
