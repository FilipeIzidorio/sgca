package com.faculdade.sgca.domain.report;

public class RelatorioAlunoCreator extends RelatorioCreator {

    private final String nomeAluno;
    private final double mediaFinal;

    public RelatorioAlunoCreator(String nomeAluno, double mediaFinal) {
        this.nomeAluno = nomeAluno;
        this.mediaFinal = mediaFinal;
    }

    @Override
    public Relatorio criarRelatorio() {
        return new RelatorioAluno(nomeAluno, mediaFinal);
    }
}
