package com.faculdade.sgca.application.mapper;

import com.faculdade.sgca.application.dto.NotaDTO;
import com.faculdade.sgca.domain.model.Avaliacao;
import com.faculdade.sgca.domain.model.Matricula;
import com.faculdade.sgca.domain.model.Nota;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotaMapper {

    // Entidade → DTO
    @Mapping(source = "avaliacao.id", target = "avaliacaoId")
    @Mapping(source = "matricula.id", target = "matriculaId")
    NotaDTO toDTO(Nota entity);

    // DTO → Entidade
    @Mapping(target = "avaliacao", expression = "java(avaliacaoFromId(dto.getAvaliacaoId()))")
    @Mapping(target = "matricula", expression = "java(matriculaFromId(dto.getMatriculaId()))")
    Nota toEntity(NotaDTO dto);

    // Métodos auxiliares
    default Avaliacao avaliacaoFromId(Long id) {
        if (id == null) return null;
        Avaliacao a = new Avaliacao();
        a.setId(id);
        return a;
    }

    default Matricula matriculaFromId(Long id) {
        if (id == null) return null;
        Matricula m = new Matricula();
        m.setId(id);
        return m;
    }
}
