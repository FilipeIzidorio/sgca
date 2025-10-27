package com.faculdade.sgca.application.mapper;

import com.faculdade.sgca.application.dto.CursoDTO;
import com.faculdade.sgca.domain.model.Curso;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CursoMapper {
    CursoDTO toDTO(Curso entity);
    Curso toEntity(CursoDTO dto);
}
