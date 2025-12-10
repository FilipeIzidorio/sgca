package com.faculdade.sgca.application.service;

import com.faculdade.sgca.application.dto.UsuarioDTO;
import com.faculdade.sgca.domain.model.PerfilUsuario;
import com.faculdade.sgca.domain.model.Usuario;
import com.faculdade.sgca.infrastructure.repository.UsuarioRepository;
import stub.UsuarioStub;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService service;

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void criar() {
        Usuario entity = UsuarioStub.entity();

        when(repository.findByEmail(any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("senhaCriptografada");
        when(repository.save(any())).thenReturn(entity);

        UsuarioDTO dto = service.criar(
                "Admin",
                "admin@email.com",
                "123456",
                PerfilUsuario.ADMIN
        );

        assertEquals("Admin", dto.getNome());
    }

    @Test
    void atualizar() {
        Usuario entity = UsuarioStub.entity();

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(passwordEncoder.encode(any())).thenReturn("novaSenha");
        when(repository.save(any())).thenReturn(entity);

        UsuarioDTO dto = service.atualizar(
                1L,
                "Novo Nome",
                "novo@email.com",
                "novaSenha",
                PerfilUsuario.ADMIN
        );

        assertEquals("Novo Nome", dto.getNome());
    }

    @Test
    void excluir() {
        when(repository.existsById(1L)).thenReturn(true);
        service.excluir(1L);
        verify(repository).deleteById(1L);
    }
}
