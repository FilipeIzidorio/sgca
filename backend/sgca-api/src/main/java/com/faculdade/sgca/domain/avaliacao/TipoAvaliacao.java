package com.faculdade.sgca.domain.avaliacao;

public interface TipoAvaliacao {
    String getDescricao();
    double calcularNotaFinal(double valorObtido, double peso);
}
