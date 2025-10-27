package com.faculdade.sgca.application.mapper;

import com.faculdade.sgca.application.dto.DisciplinaDTO;
import com.faculdade.sgca.domain.model.Disciplina;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DisciplinaMapper {
    DisciplinaDTO toDTO(Disciplina entity);

    Disciplina toEntity(DisciplinaDTO dto);


}
