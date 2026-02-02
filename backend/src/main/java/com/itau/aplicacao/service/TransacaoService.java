package com.itau.aplicacao.service;

import com.itau.aplicacao.dto.TransacaoRequest;
import com.itau.aplicacao.model.Transacao;
import com.itau.aplicacao.store.TransacaoStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final TransacaoStore transacaoStore;

    public void registrar(TransacaoRequest request) {
        Transacao transacao = new Transacao(request.getValor(), request.getDataHora());
        transacaoStore.adicionar(transacao);
    }

    public void limparTransacoes() {
        transacaoStore.limparTodas();
    }
}
