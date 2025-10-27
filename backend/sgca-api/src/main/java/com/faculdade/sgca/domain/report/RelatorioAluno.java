package com.faculdade.sgca.domain.report;

public class RelatorioAluno implements Relatorio {

    private final String nomeAluno;
    private final double mediaFinal;

    public RelatorioAluno(String nomeAluno, double mediaFinal) {
        this.nomeAluno = nomeAluno;
        this.mediaFinal = mediaFinal;
    }

    @Override
    public String gerar() {
        return """
                ===== Relatório de Desempenho do Aluno =====
                Nome: %s
                Média Final: %.2f
                ---------------------------------------------
                """.formatted(nomeAluno, mediaFinal);
    }
}
