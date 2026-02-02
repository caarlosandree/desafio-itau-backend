package com.itau.aplicacao.store;

import com.itau.aplicacao.model.Transacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TransacaoStore")
class TransacaoStoreTest {

    private TransacaoStore store;

    @BeforeEach
    void setUp() {
        store = new TransacaoStore();
    }

    @Nested
    @DisplayName("adicionar")
    class Adicionar {

        @Test
        void deveAdicionarTransacao() {
            Transacao transacao = new Transacao(BigDecimal.TEN, OffsetDateTime.now(ZoneOffset.UTC));

            store.adicionar(transacao);

            List<Transacao> todas = store.obterTodas();
            assertThat(todas).hasSize(1);
            assertThat(todas.get(0).getValor()).isEqualByComparingTo(BigDecimal.TEN);
        }

        @Test
        void deveAdicionarMultiplasTransacoes() {
            store.adicionar(new Transacao(BigDecimal.ONE, OffsetDateTime.now(ZoneOffset.UTC)));
            store.adicionar(new Transacao(BigDecimal.valueOf(2), OffsetDateTime.now(ZoneOffset.UTC)));

            assertThat(store.obterTodas()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("obterTodas")
    class ObterTodas {

        @Test
        void deveRetornarListaVaziaQuandoNaoHaTransacoes() {
            List<Transacao> todas = store.obterTodas();

            assertThat(todas).isEmpty();
        }

        @Test
        void deveRetornarCopiaNaoModificavel() {
            store.adicionar(new Transacao(BigDecimal.ONE, OffsetDateTime.now(ZoneOffset.UTC)));
            List<Transacao> todas = store.obterTodas();

            assertThat(todas).isUnmodifiable();
        }
    }

    @Nested
    @DisplayName("limparTodas")
    class LimparTodas {

        @Test
        void deveRemoverTodasAsTransacoes() {
            store.adicionar(new Transacao(BigDecimal.ONE, OffsetDateTime.now(ZoneOffset.UTC)));
            store.adicionar(new Transacao(BigDecimal.TEN, OffsetDateTime.now(ZoneOffset.UTC)));

            store.limparTodas();

            assertThat(store.obterTodas()).isEmpty();
        }

        @Test
        void naoFalhaQuandoStoreJaEstaVazio() {
            store.limparTodas();

            assertThat(store.obterTodas()).isEmpty();
        }
    }
}
