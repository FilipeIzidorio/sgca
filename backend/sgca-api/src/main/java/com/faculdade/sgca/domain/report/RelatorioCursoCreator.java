package com.faculdade.sgca.domain.report;

public class RelatorioCursoCreator extends RelatorioCreator {

    private final String nomeCurso;
    private final int qtdAlunos;

    public RelatorioCursoCreator(String nomeCurso, int qtdAlunos) {
        this.nomeCurso = nomeCurso;
        this.qtdAlunos = qtdAlunos;
    }

    @Override
    public Relatorio criarRelatorio() {
        return new RelatorioCurso(nomeCurso, qtdAlunos);
    }
}
