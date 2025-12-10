package stub;

import com.faculdade.sgca.application.dto.PresencaDTO;
import com.faculdade.sgca.domain.model.Matricula;
import com.faculdade.sgca.domain.model.Presenca;
import com.faculdade.sgca.domain.model.Turma;

import java.time.LocalDate;

public class PresencaStub {

    public static PresencaDTO dto() {
        PresencaDTO dto = new PresencaDTO();
        dto.setId(1L);
        dto.setMatriculaId(10L);
        dto.setTurmaId(20L);

        dto.setPresente(true);
        return dto;
    }

    public static Presenca entity() {
        Presenca p = new Presenca();
        p.setId(1L);

        p.setPresente(true);

        Matricula matricula = new Matricula();
        matricula.setId(10L);

        Turma turma = new Turma();
        turma.setId(20L);

        p.setMatricula(matricula);
        p.setTurma(turma);

        return p;
    }

    public static Presenca nova() {
        Presenca p = new Presenca();

        p.setPresente(true);

        Matricula matricula = new Matricula();
        matricula.setId(10L);

        Turma turma = new Turma();
        turma.setId(20L);

        p.setMatricula(matricula);
        p.setTurma(turma);

        return p;
    }
}
