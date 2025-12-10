package stub;

import com.faculdade.sgca.application.dto.NotaDTO;
import com.faculdade.sgca.domain.model.Matricula;
import com.faculdade.sgca.domain.model.Nota;
import com.faculdade.sgca.domain.model.Turma;

public class NotaStub {

    public static NotaDTO dto() {
        NotaDTO dto = new NotaDTO();
        dto.setId(1L);
        dto.setMatriculaId(10L);
        dto.setValor(8.5);
        return dto;
    }

    public static Nota entity() {
        Nota n = new Nota();
        n.setId(1L);
        n.setValor(8.5);

        Matricula matricula = new Matricula();
        matricula.setId(10L);

        Turma turma = new Turma();
        turma.setId(20L);

        n.setMatricula(matricula);


        return n;
    }

    public static Nota nova() {
        Nota n = new Nota();
        n.setValor(7.0);

        Matricula matricula = new Matricula();
        matricula.setId(10L);

        Turma turma = new Turma();
        turma.setId(20L);

        n.setMatricula(matricula);


        return n;
    }
}
