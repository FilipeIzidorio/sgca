package stub;


import com.faculdade.sgca.application.dto.AlunoDTO;
import com.faculdade.sgca.domain.model.Aluno;

import java.time.LocalDate;

public class AlunoStub {

    private AlunoStub() {}

    public static AlunoDTO dtoValido() {
        AlunoDTO dto = new AlunoDTO();
        dto.setId(1L);
        dto.setNome("Maria Silva");
        dto.setEmail("maria@email.com");
        dto.setCpf("12345678900");
        dto.setStatus("ATIVO");
        dto.setDataNascimento(LocalDate.of(2000, 1, 1));
        return dto;
    }

    public static AlunoDTO dtoAtualizado() {
        AlunoDTO dto = dtoValido();
        dto.setNome("Maria Atualizada");
        return dto;
    }

    public static Aluno entityValida() {
        Aluno aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("Maria Silva");
        aluno.setEmail("maria@email.com");
        aluno.setCpf("12345678900");
        aluno.setStatus("ATIVO");
        aluno.setDataNascimento(LocalDate.of(2000, 1, 1));
        return aluno;
    }
}

