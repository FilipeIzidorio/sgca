package com.faculdade.sgca.application.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;



import com.faculdade.sgca.application.dto.AvaliacaoDTO;
import com.faculdade.sgca.application.mapper.AvaliacaoMapper;

import com.faculdade.sgca.infrastructure.repository.AvaliacaoRepository;
import stub.AvaliacaoStub;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;


import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvaliacaoServiceTest {

    @InjectMocks
    private AvaliacaoService service;

    @Mock
    private AvaliacaoRepository repository;

    @Mock
    private AvaliacaoMapper mapper;

    @Test
    void listar() {
        when(repository.findAll()).thenReturn(List.of(AvaliacaoStub.entity()));
        when(mapper.toDTO(any())).thenReturn(AvaliacaoStub.dto());

        List<AvaliacaoDTO> lista = service.listar();

        assertFalse(lista.isEmpty());
        assertEquals("Prova Final", lista.get(0).getTitulo());
        verify(repository).findAll();
    }

    @Test
    void buscarPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(AvaliacaoStub.entity()));
        when(mapper.toDTO(any())).thenReturn(AvaliacaoStub.dto());

        AvaliacaoDTO dto = service.buscarPorId(1L);

        assertNotNull(dto);
        assertEquals("Prova Final", dto.getTitulo());
    }

    @Test
    void criar() {
        when(repository.findByTurmaId(10L)).thenReturn(List.of());
        when(mapper.toEntity(any())).thenReturn(AvaliacaoStub.novaEntity());
        when(mapper.toDTO(any())).thenReturn(AvaliacaoStub.dto());
        when(repository.save(any())).thenReturn(AvaliacaoStub.entity());

        AvaliacaoDTO criado = service.criar(AvaliacaoStub.dto());

        assertNotNull(criado);
        verify(repository).save(any());
    }

    @Test
    void atualizar() {
        when(repository.findById(1L)).thenReturn(Optional.of(AvaliacaoStub.entity()));
        when(repository.save(any())).thenReturn(AvaliacaoStub.entity());
        when(mapper.toDTO(any())).thenReturn(AvaliacaoStub.dto());

        AvaliacaoDTO atualizado = service.atualizar(1L, AvaliacaoStub.dto());

        assertEquals("Prova Final", atualizado.getTitulo());
    }

    @Test
    void excluir() {
        when(repository.existsById(1L)).thenReturn(true);
        service.excluir(1L);
        verify(repository).deleteById(1L);
    }
}
