package com.faculdade.sgca.application.mapper;

import com.faculdade.sgca.application.dto.AlunoDTO;
import com.faculdade.sgca.domain.model.Aluno;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AlunoMapper {
    AlunoDTO toDTO(Aluno entity);
    Aluno toEntity(AlunoDTO dto);
}
