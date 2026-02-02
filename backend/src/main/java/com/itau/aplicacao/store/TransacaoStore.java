package com.itau.aplicacao.store;

import com.itau.aplicacao.model.Transacao;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class TransacaoStore {

    private final List<Transacao> transacoes = new CopyOnWriteArrayList<>();

    public void adicionar(Transacao transacao) {
        transacoes.add(transacao);
    }

    public List<Transacao> obterTodas() {
        return List.copyOf(transacoes);
    }

    public void limparTodas() {
        transacoes.clear();
    }
}
