package stub;

import com.faculdade.sgca.application.dto.DisciplinaDTO;
import com.faculdade.sgca.domain.model.Curso;
import com.faculdade.sgca.domain.model.Disciplina;

public class DisciplinaStub {

    public static DisciplinaDTO dto() {
        DisciplinaDTO dto = new DisciplinaDTO();
        dto.setId(1L);
        dto.setCodigo("POO");
        dto.setNome("Programação Orientada a Objetos");
        dto.setCargaHoraria(80);
        dto.setCursoId(10L);
        return dto;
    }

    public static Disciplina entity() {
        Disciplina d = new Disciplina();
        d.setId(1L);
        d.setCodigo("POO");
        d.setNome("Programação Orientada a Objetos");
        d.setCargaHoraria(80);

        Curso curso = new Curso();
        curso.setId(10L);
        d.setCurso(curso);

        return d;
    }

    public static Disciplina nova() {
        Disciplina d = new Disciplina();
        d.setCodigo("BD");
        d.setNome("Banco de Dados");
        d.setCargaHoraria(60);

        Curso curso = new Curso();
        curso.setId(10L);
        d.setCurso(curso);

        return d;
    }
}
