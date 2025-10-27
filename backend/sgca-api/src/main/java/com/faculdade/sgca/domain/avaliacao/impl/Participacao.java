package com.faculdade.sgca.domain.avaliacao.impl;

import com.faculdade.sgca.domain.avaliacao.TipoAvaliacao;

public class Participacao implements TipoAvaliacao {
    @Override
    public String getDescricao() {
        return "Participação em aula e engajamento";
    }

    @Override
    public double calcularNotaFinal(double valorObtido, double peso) {
        // Participação tem peso reduzido (máximo 8)
        double nota = Math.min(valorObtido, 8);
        return nota * (peso / 100);
    }
}