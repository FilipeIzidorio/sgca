package com.faculdade.sgca.domain.report;

public abstract class RelatorioCreator {
    public abstract Relatorio criarRelatorio();

    public String exportar() {
        Relatorio relatorio = criarRelatorio();
        return relatorio.gerar();
    }
}
