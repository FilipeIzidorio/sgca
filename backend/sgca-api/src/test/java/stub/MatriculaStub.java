package stub;


import com.faculdade.sgca.application.dto.MatriculaDTO;
import com.faculdade.sgca.domain.model.Aluno;
import com.faculdade.sgca.domain.model.Turma;
import com.faculdade.sgca.domain.model.Matricula;

public class MatriculaStub {

    public static MatriculaDTO dto() {
        MatriculaDTO dto = new MatriculaDTO();
        dto.setId(1L);
        dto.setAlunoId(10L);
        dto.setTurmaId(20L);
        dto.setSituacao("ATIVA");
        return dto;
    }

    public static Matricula entity() {
        Matricula m = new Matricula();
        m.setId(1L);
        m.setSituacao("ATIVA");

        Aluno aluno = new Aluno();
        aluno.setId(10L);

        Turma turma = new Turma();
        turma.setId(20L);

        m.setAluno(aluno);
        m.setTurma(turma);

        return m;
    }

    public static Matricula nova() {
        Matricula m = new Matricula();
        m.setSituacao("ATIVA");

        Aluno aluno = new Aluno();
        aluno.setId(10L);

        Turma turma = new Turma();
        turma.setId(20L);

        m.setAluno(aluno);
        m.setTurma(turma);

        return m;
    }
}
