package com.faculdade.sgca.domain.report;

public class RelatorioCurso implements Relatorio {

    private final String nomeCurso;
    private final int qtdAlunos;

    public RelatorioCurso(String nomeCurso, int qtdAlunos) {
        this.nomeCurso = nomeCurso;
        this.qtdAlunos = qtdAlunos;
    }

    @Override
    public String gerar() {
        return """
                ===== Relatório de Curso =====
                Curso: %s
                Total de Alunos: %d
                --------------------------------
                """.formatted(nomeCurso, qtdAlunos);
    }
}
