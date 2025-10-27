package com.faculdade.sgca.application.mapper;

import com.faculdade.sgca.application.dto.PresencaDTO;
import com.faculdade.sgca.domain.model.Matricula;
import com.faculdade.sgca.domain.model.Presenca;
import com.faculdade.sgca.domain.model.Turma;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PresencaMapper {

    // Entidade → DTO
    @Mapping(source = "turma.id", target = "turmaId")
    @Mapping(source = "matricula.id", target = "matriculaId")
    PresencaDTO toDTO(Presenca entity);

    // DTO → Entidade
    @Mapping(target = "turma", expression = "java(turmaFromId(dto.getTurmaId()))")
    @Mapping(target = "matricula", expression = "java(matriculaFromId(dto.getMatriculaId()))")
    Presenca toEntity(PresencaDTO dto);

    // Métodos auxiliares
    default Turma turmaFromId(Long id) {
        if (id == null) return null;
        Turma t = new Turma();
        t.setId(id);
        return t;
    }

    default Matricula matriculaFromId(Long id) {
        if (id == null) return null;
        Matricula m = new Matricula();
        m.setId(id);
        return m;
    }
}
