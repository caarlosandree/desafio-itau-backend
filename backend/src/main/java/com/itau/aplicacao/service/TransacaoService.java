package com.itau.aplicacao.service;

import com.itau.aplicacao.dto.EstatisticasResponse;
import com.itau.aplicacao.dto.TransacaoRequest;
import com.itau.aplicacao.model.Transacao;
import com.itau.aplicacao.store.TransacaoStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.DoubleSummaryStatistics;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private static final int JANELA_SEGUNDOS = 60;

    private final TransacaoStore transacaoStore;

    public void registrar(TransacaoRequest request) {
        Transacao transacao = new Transacao(request.getValor(), request.getDataHora());
        transacaoStore.adicionar(transacao);
    }

    public void limparTransacoes() {
        transacaoStore.limparTodas();
    }

    public EstatisticasResponse calcularEstatisticas() {
        Instant cutoff = Instant.now().minus(JANELA_SEGUNDOS, ChronoUnit.SECONDS);

        DoubleSummaryStatistics stats = transacaoStore.obterTodas().stream()
                .filter(t -> t.getDataHora().toInstant().isAfter(cutoff) || t.getDataHora().toInstant().equals(cutoff))
                .collect(Collectors.summarizingDouble(t -> t.getValor().doubleValue()));

        if (stats.getCount() == 0) {
            return new EstatisticasResponse(0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        BigDecimal sum = BigDecimal.valueOf(stats.getSum());
        BigDecimal avg = BigDecimal.valueOf(stats.getAverage()).setScale(6, RoundingMode.HALF_UP);
        BigDecimal min = BigDecimal.valueOf(stats.getMin());
        BigDecimal max = BigDecimal.valueOf(stats.getMax());

        return new EstatisticasResponse(stats.getCount(), sum, avg, min, max);
    }
}
