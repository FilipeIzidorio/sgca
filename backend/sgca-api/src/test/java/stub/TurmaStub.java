package stub;

import com.faculdade.sgca.application.dto.TurmaDTO;
import com.faculdade.sgca.domain.model.Disciplina;
import com.faculdade.sgca.domain.model.Usuario;
import com.faculdade.sgca.domain.model.Turma;


public class TurmaStub {

    public static TurmaDTO dto() {
        TurmaDTO dto = new TurmaDTO();
        dto.setId(1L);
        dto.setDisciplinaId(10L);
        dto.setProfessorId(20L);
        dto.setPeriodo("2025.1");
        return dto;
    }

    public static Turma entity() {
        Turma turma = new Turma();
        turma.setId(1L);
        turma.setPeriodo("2025.1");

        Disciplina disciplina = new Disciplina();
        disciplina.setId(10L);

        Usuario professor = new Usuario();
        professor.setId(20L);

        turma.setDisciplina(disciplina);
        turma.setProfessor(professor);

        return turma;
    }

    public static Turma nova() {
        Turma turma = new Turma();
        turma.setPeriodo("2025.1");

        Disciplina disciplina = new Disciplina();
        disciplina.setId(10L);

        Usuario professor = new Usuario();
        professor.setId(20L);

        turma.setDisciplina(disciplina);
        turma.setProfessor(professor);

        return turma;
    }
}
