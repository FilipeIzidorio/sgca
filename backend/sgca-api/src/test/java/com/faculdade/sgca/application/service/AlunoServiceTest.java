package com.faculdade.sgca.application.service;


import com.faculdade.sgca.application.dto.AlunoDTO;
import com.faculdade.sgca.application.mapper.AlunoMapper;
import com.faculdade.sgca.domain.model.Aluno;
import stub.AlunoStub;
import com.faculdade.sgca.infrastructure.repository.AlunoRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {

    @Mock private AlunoRepository repository;
    @Mock
    private AlunoMapper mapper;

    @InjectMocks
    private AlunoService service;

    @Test
    void deveCriarAlunoComSucesso() {
        AlunoDTO dto = AlunoStub.dtoValido();
        Aluno entity = AlunoStub.entityValida();

        when(repository.existsByCpf(dto.getCpf())).thenReturn(false);
        when(repository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDTO(entity)).thenReturn(dto);

        AlunoDTO salvo = service.criar(dto);

        assertNotNull(salvo);
        assertEquals("Maria Silva", salvo.getNome());
        verify(repository).save(entity);
    }

    @Test
    void deveAtualizarAluno() {
        Aluno entity = AlunoStub.entityValida();
        AlunoDTO atualizado = AlunoStub.dtoAtualizado();

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(any())).thenReturn(entity);
        when(mapper.toDTO(any())).thenReturn(atualizado);

        AlunoDTO result = service.atualizar(1L, atualizado);

        assertEquals("Maria Atualizada", result.getNome());
    }

    @Test
    void deveExcluirAluno() {
        when(repository.existsById(1L)).thenReturn(true);

        service.excluir(1L);

        verify(repository).deleteById(1L);
    }
}
