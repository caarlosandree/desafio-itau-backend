package com.itau.aplicacao.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Configuração da janela de tempo (em segundos) usada no cálculo das estatísticas.
 * Valor inicial vem de application.properties; pode ser alterado em runtime via endpoint.
 */
@Component
public class JanelaEstatisticasConfig {

    private final AtomicInteger janelaSegundos;

    public JanelaEstatisticasConfig(
            @Value("${app.estatisticas.janela-segundos:60}") int janelaSegundosInicial
    ) {
        this.janelaSegundos = new AtomicInteger(janelaSegundosInicial);
    }

    public int getJanelaSegundos() {
        return janelaSegundos.get();
    }

    public void setJanelaSegundos(int segundos) {
        this.janelaSegundos.set(segundos);
    }
}
