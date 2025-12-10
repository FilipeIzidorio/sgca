package stub;


import com.faculdade.sgca.application.dto.AvaliacaoDTO;
import com.faculdade.sgca.domain.model.Avaliacao;

public class AvaliacaoStub {

    public static AvaliacaoDTO dto() {
        AvaliacaoDTO dto = new AvaliacaoDTO();
        dto.setId(1L);
        dto.setTitulo("Prova Final");
        dto.setPeso(40.0);
        dto.setTipo("PROVA");
        dto.setTurmaId(10L);
        return dto;
    }

    public static Avaliacao entity() {
        Avaliacao a = new Avaliacao();
        a.setId(1L);
        a.setTitulo("Prova Final");
        a.setPeso(40.0);
        a.setTipo("PROVA");
        a.setTurmaId(10L);
        return a;
    }

    public static Avaliacao novaEntity() {
        Avaliacao a = new Avaliacao();
        a.setTitulo("Prova 2");
        a.setPeso(30.0);
        a.setTipo("TRABALHO");
        a.setTurmaId(10L);
        return a;
    }
}
