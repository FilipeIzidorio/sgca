package stub;


import com.faculdade.sgca.application.dto.CursoDTO;
import com.faculdade.sgca.domain.model.Curso;

public class CursoStub {

    public static CursoDTO dto() {
        CursoDTO dto = new CursoDTO();
        dto.setId(1L);
        dto.setCodigo("ADS");
        dto.setNome("Análise e Desenvolvimento de Sistemas");
        dto.setCargaHoraria(2400);
        dto.setDescricao("Curso de tecnologia");
        return dto;
    }

    public static Curso entity() {
        Curso c = new Curso();
        c.setId(1L);
        c.setCodigo("ADS");
        c.setNome("Análise e Desenvolvimento de Sistemas");
        c.setCargaHoraria(2400);
        c.setDescricao("Curso de tecnologia");
        return c;
    }

    public static Curso novo() {
        Curso c = new Curso();
        c.setCodigo("SI");
        c.setNome("Sistemas de Informação");
        c.setCargaHoraria(2200);
        c.setDescricao("Curso de sistemas");
        return c;
    }
}
