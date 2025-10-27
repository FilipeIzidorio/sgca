package com.faculdade.sgca.application.mapper;

import com.faculdade.sgca.application.dto.TurmaDTO;
import com.faculdade.sgca.domain.model.Turma;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TurmaMapper {
    TurmaDTO toDTO(Turma entity);
    Turma toEntity(TurmaDTO dto);


}
