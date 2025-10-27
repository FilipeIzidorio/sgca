package com.faculdade.sgca.application.mapper;

import com.faculdade.sgca.application.dto.UsuarioDTO;
import com.faculdade.sgca.domain.model.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    UsuarioDTO toDTO(Usuario entity);
    Usuario toEntity(UsuarioDTO dto);
}
