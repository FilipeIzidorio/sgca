package com.faculdade.sgca.application.service;

import com.faculdade.sgca.application.dto.CursoDTO;
import com.faculdade.sgca.application.mapper.CursoMapper;
import com.faculdade.sgca.domain.model.Curso;
import com.faculdade.sgca.infrastructure.repository.CursoRepository;
import stub.CursoStub;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CursoServiceTest {

    @InjectMocks
    private CursoService service;

    @Mock
    private CursoRepository repository;

    @Mock
    private CursoMapper mapper;

    @Test
    void listarTodos() {
        when(repository.findAll()).thenReturn(List.of(CursoStub.entity()));
        when(mapper.toDTO(any())).thenReturn(CursoStub.dto());

        List<CursoDTO> lista = service.listarTodos();

        assertFalse(lista.isEmpty());
        assertEquals("ADS", lista.get(0).getCodigo());
        verify(repository).findAll();
    }

    @Test
    void buscarPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(CursoStub.entity()));
        when(mapper.toDTO(any())).thenReturn(CursoStub.dto());

        CursoDTO dto = service.buscarPorId(1L);

        assertNotNull(dto);
        assertEquals("Análise e Desenvolvimento de Sistemas", dto.getNome());
    }

    @Test
    void criar() {
        when(repository.existsByCodigo("ADS")).thenReturn(false);
        when(mapper.toEntity(any())).thenReturn(CursoStub.novo());
        when(repository.save(any())).thenReturn(CursoStub.entity());
        when(mapper.toDTO(any())).thenReturn(CursoStub.dto());

        CursoDTO criado = service.criar(CursoStub.dto());

        assertNotNull(criado);
        verify(repository).save(any());
    }

    @Test
    void atualizar() {
        when(repository.findById(1L)).thenReturn(Optional.of(CursoStub.entity()));
        when(repository.save(any())).thenReturn(CursoStub.entity());
        when(mapper.toDTO(any())).thenReturn(CursoStub.dto());

        CursoDTO atualizado = service.atualizar(1L, CursoStub.dto());

        assertEquals("ADS", atualizado.getCodigo());
    }

    @Test
    void excluir() {
        when(repository.existsById(1L)).thenReturn(true);

        service.excluir(1L);

        verify(repository).deleteById(1L);
    }
}
