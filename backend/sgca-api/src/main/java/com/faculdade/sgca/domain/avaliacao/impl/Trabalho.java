package com.faculdade.sgca.domain.avaliacao.impl;

import com.faculdade.sgca.domain.avaliacao.TipoAvaliacao;

public class Trabalho implements TipoAvaliacao {
    @Override
    public String getDescricao() {
        return "Trabalho prático ou teórico";
    }

    @Override
    public double calcularNotaFinal(double valorObtido, double peso) {
        return valorObtido * (peso / 100);
    }
}
