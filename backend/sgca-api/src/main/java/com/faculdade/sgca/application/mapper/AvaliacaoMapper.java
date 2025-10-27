package com.faculdade.sgca.application.mapper;

import com.faculdade.sgca.application.dto.AvaliacaoDTO;
import com.faculdade.sgca.domain.model.Avaliacao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AvaliacaoMapper {

    AvaliacaoDTO toDTO(Avaliacao entity);
    Avaliacao toEntity(AvaliacaoDTO dto);
}
