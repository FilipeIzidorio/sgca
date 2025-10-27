package com.faculdade.sgca.application.mapper;

import com.faculdade.sgca.application.dto.MatriculaDTO;
import com.faculdade.sgca.domain.model.Aluno;
import com.faculdade.sgca.domain.model.Matricula;
import com.faculdade.sgca.domain.model.Turma;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MatriculaMapper {

    // Entidade -> DTO
    @Mapping(source = "aluno.id", target = "alunoId")
    @Mapping(source = "turma.id", target = "turmaId")
    MatriculaDTO toDTO(Matricula entity);

    // DTO -> Entidade
    @Mapping(target = "aluno", expression = "java(alunoFromId(dto.getAlunoId()))")
    @Mapping(target = "turma", expression = "java(turmaFromId(dto.getTurmaId()))")
    Matricula toEntity(MatriculaDTO dto);

    // Métodos auxiliares
    default Aluno alunoFromId(Long id) {
        if (id == null) return null;
        Aluno aluno = new Aluno();
        aluno.setId(id);
        return aluno;
    }

    default Turma turmaFromId(Long id) {
        if (id == null) return null;
        Turma turma = new Turma();
        turma.setId(id);
        return turma;
    }
}
