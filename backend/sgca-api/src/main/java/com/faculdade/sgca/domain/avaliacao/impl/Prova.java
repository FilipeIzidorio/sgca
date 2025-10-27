package com.faculdade.sgca.domain.avaliacao.impl;

import com.faculdade.sgca.domain.avaliacao.TipoAvaliacao;

public class Prova implements TipoAvaliacao {
    @Override
    public String getDescricao() {
        return "Prova escrita individual";
    }

    @Override
    public double calcularNotaFinal(double valorObtido, double peso) {
        return valorObtido * (peso / 100);
    }
}
