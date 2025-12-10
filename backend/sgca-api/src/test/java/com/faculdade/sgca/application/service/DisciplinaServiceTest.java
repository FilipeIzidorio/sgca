package com.faculdade.sgca.application.service;

import com.faculdade.sgca.application.dto.DisciplinaDTO;
import com.faculdade.sgca.application.mapper.DisciplinaMapper;
import com.faculdade.sgca.domain.model.Curso;

import com.faculdade.sgca.infrastructure.repository.CursoRepository;
import com.faculdade.sgca.infrastructure.repository.DisciplinaRepository;
import stub.DisciplinaStub;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisciplinaServiceTest {

    @InjectMocks
    private DisciplinaService service;

    @Mock
    private DisciplinaRepository repository;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private DisciplinaMapper mapper;

    @Test
    void listarTodas() {
        when(repository.findAll()).thenReturn(List.of(DisciplinaStub.entity()));
        when(mapper.toDTO(any())).thenReturn(DisciplinaStub.dto());

        List<DisciplinaDTO> lista = service.listarTodas();

        assertFalse(lista.isEmpty());
        assertEquals("POO", lista.get(0).getCodigo());
        verify(repository).findAll();
    }

    @Test
    void buscarPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(DisciplinaStub.entity()));
        when(mapper.toDTO(any())).thenReturn(DisciplinaStub.dto());

        DisciplinaDTO dto = service.buscarPorId(1L);

        assertNotNull(dto);
        assertEquals("Programação Orientada a Objetos", dto.getNome());
    }

    @Test
    void buscarPorCurso() {
        when(repository.findByCurso_Id(10L)).thenReturn(List.of(DisciplinaStub.entity()));
        when(mapper.toDTO(any())).thenReturn(DisciplinaStub.dto());

        List<DisciplinaDTO> lista = service.buscarPorCurso(10L);

        assertEquals(1, lista.size());
        assertEquals("POO", lista.get(0).getCodigo());
    }

    @Test
    void criar() {
        when(repository.existsByCodigo("POO")).thenReturn(false);
        when(cursoRepository.findById(10L))
                .thenReturn(Optional.of(new Curso()));
        when(mapper.toEntity(any())).thenReturn(DisciplinaStub.nova());
        when(repository.save(any())).thenReturn(DisciplinaStub.entity());
        when(mapper.toDTO(any())).thenReturn(DisciplinaStub.dto());

        DisciplinaDTO criada = service.criar(DisciplinaStub.dto());

        assertNotNull(criada);
        verify(repository).save(any());
    }

    @Test
    void atualizar() {
        when(repository.findById(1L)).thenReturn(Optional.of(DisciplinaStub.entity()));
        when(repository.existsByCodigo(any())).thenReturn(false);
        when(repository.save(any())).thenReturn(DisciplinaStub.entity());
        when(mapper.toDTO(any())).thenReturn(DisciplinaStub.dto());
        when(cursoRepository.findById(any()))
                .thenReturn(Optional.of(new Curso()));

        DisciplinaDTO atualizada = service.atualizar(1L, DisciplinaStub.dto());

        assertEquals("POO", atualizada.getCodigo());
    }

    @Test
    void excluir() {
        when(repository.existsById(1L)).thenReturn(true);

        service.excluir(1L);

        verify(repository).deleteById(1L);
    }
}
