package com.itau.aplicacao.service;

import com.itau.aplicacao.dto.EstatisticasResponse;
import com.itau.aplicacao.dto.TransacaoRequest;
import com.itau.aplicacao.model.Transacao;
import com.itau.aplicacao.store.TransacaoStore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransacaoService")
class TransacaoServiceTest {

    @Mock
    private TransacaoStore transacaoStore;

    @InjectMocks
    private TransacaoService transacaoService;

    @Nested
    @DisplayName("registrar")
    class Registrar {

        @Test
        void deveChamarStoreComTransacaoConvertida() {
            OffsetDateTime dataHora = OffsetDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
            TransacaoRequest request = new TransacaoRequest();
            request.setValor(BigDecimal.valueOf(15.50));
            request.setDataHora(dataHora);

            transacaoService.registrar(request);

            ArgumentCaptor<Transacao> captor = ArgumentCaptor.forClass(Transacao.class);
            verify(transacaoStore).adicionar(captor.capture());
            Transacao capturada = captor.getValue();
            assertThat(capturada.getValor()).isEqualByComparingTo(BigDecimal.valueOf(15.50));
            assertThat(capturada.getDataHora()).isEqualTo(dataHora);
        }
    }

    @Nested
    @DisplayName("limparTransacoes")
    class LimparTransacoes {

        @Test
        void deveChamarLimparTodasNoStore() {
            transacaoService.limparTransacoes();

            verify(transacaoStore).limparTodas();
        }
    }

    @Nested
    @DisplayName("calcularEstatisticas")
    class CalcularEstatisticas {

        @Test
        void deveRetornarZerosQuandoNaoHaTransacoes() {
            when(transacaoStore.obterTodas()).thenReturn(List.of());

            EstatisticasResponse result = transacaoService.calcularEstatisticas();

            assertThat(result.getCount()).isZero();
            assertThat(result.getSum()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(result.getAvg()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(result.getMin()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(result.getMax()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        void deveCalcularEstatisticasComTransacoesNaJanela() {
            Instant agora = Instant.now();
            OffsetDateTime dt1 = OffsetDateTime.ofInstant(agora, ZoneOffset.UTC);
            OffsetDateTime dt2 = OffsetDateTime.ofInstant(agora.minusSeconds(30), ZoneOffset.UTC);
            when(transacaoStore.obterTodas()).thenReturn(List.of(
                    new Transacao(BigDecimal.valueOf(10), dt1),
                    new Transacao(BigDecimal.valueOf(20), dt2)
            ));

            EstatisticasResponse result = transacaoService.calcularEstatisticas();

            assertThat(result.getCount()).isEqualTo(2);
            assertThat(result.getSum()).isEqualByComparingTo(BigDecimal.valueOf(30));
            assertThat(result.getAvg()).isEqualByComparingTo(BigDecimal.valueOf(15));
            assertThat(result.getMin()).isEqualByComparingTo(BigDecimal.valueOf(10));
            assertThat(result.getMax()).isEqualByComparingTo(BigDecimal.valueOf(20));
        }

        @Test
        void deveIgnorarTransacoesForaDaJanelaDe60Segundos() {
            Instant agora = Instant.now();
            OffsetDateTime dentro = OffsetDateTime.ofInstant(agora, ZoneOffset.UTC);
            OffsetDateTime fora = OffsetDateTime.ofInstant(agora.minusSeconds(61), ZoneOffset.UTC);
            when(transacaoStore.obterTodas()).thenReturn(List.of(
                    new Transacao(BigDecimal.valueOf(100), dentro),
                    new Transacao(BigDecimal.valueOf(200), fora)
            ));

            EstatisticasResponse result = transacaoService.calcularEstatisticas();

            assertThat(result.getCount()).isEqualTo(1);
            assertThat(result.getSum()).isEqualByComparingTo(BigDecimal.valueOf(100));
            assertThat(result.getMin()).isEqualByComparingTo(BigDecimal.valueOf(100));
            assertThat(result.getMax()).isEqualByComparingTo(BigDecimal.valueOf(100));
        }

        @Test
        void deveIncluirTransacaoDentroDaJanelaDe60Segundos() {
            Instant agora = Instant.now();
            OffsetDateTime dentro = OffsetDateTime.ofInstant(agora.minusSeconds(59), ZoneOffset.UTC);
            when(transacaoStore.obterTodas()).thenReturn(List.of(
                    new Transacao(BigDecimal.valueOf(5), dentro)
            ));

            EstatisticasResponse result = transacaoService.calcularEstatisticas();

            assertThat(result.getCount()).isEqualTo(1);
            assertThat(result.getSum()).isEqualByComparingTo(BigDecimal.valueOf(5));
        }

        @Test
        void deveCalcularMediaComSeisCasasDecimais() {
            Instant agora = Instant.now();
            when(transacaoStore.obterTodas()).thenReturn(List.of(
                    new Transacao(BigDecimal.valueOf(10), OffsetDateTime.ofInstant(agora, ZoneOffset.UTC)),
                    new Transacao(BigDecimal.valueOf(11), OffsetDateTime.ofInstant(agora, ZoneOffset.UTC))
            ));

            EstatisticasResponse result = transacaoService.calcularEstatisticas();

            assertThat(result.getAvg()).isEqualByComparingTo(BigDecimal.valueOf(10.5));
            assertThat(result.getAvg().scale()).isLessThanOrEqualTo(6);
        }
    }
}
