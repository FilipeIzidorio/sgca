package com.faculdade.sgca.application.service;

import com.faculdade.sgca.application.dto.PresencaDTO;
import com.faculdade.sgca.application.mapper.PresencaMapper;
import com.faculdade.sgca.domain.model.Matricula;
import com.faculdade.sgca.domain.model.Presenca;
import com.faculdade.sgca.domain.model.Turma;
import com.faculdade.sgca.infrastructure.repository.MatriculaRepository;
import com.faculdade.sgca.infrastructure.repository.PresencaRepository;
import com.faculdade.sgca.infrastructure.repository.TurmaRepository;
import stub.PresencaStub;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PresencaServiceTest {

    @InjectMocks
    private PresencaService service;

    @Mock
    private PresencaRepository repository;

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private TurmaRepository turmaRepository;

    @Mock
    private PresencaMapper mapper;

    @Test
    void listarTodas() {
        when(repository.findAll()).thenReturn(List.of(PresencaStub.entity()));
        when(mapper.toDTO(any())).thenReturn(PresencaStub.dto());

        List<PresencaDTO> lista = service.listarTodas();

        assertFalse(lista.isEmpty());
        assertTrue(lista.get(0).isPresente());
    }

    @Test
    void buscarPorId() {
        when(repository.findById(1L))
                .thenReturn(Optional.of(PresencaStub.entity()));
        when(mapper.toDTO(any()))
                .thenReturn(PresencaStub.dto());

        PresencaDTO dto = service.buscarPorId(1L);

        assertNotNull(dto);
        assertEquals(10L, dto.getMatriculaId());
    }

    @Test
    void buscarPorTurma() {
        when(repository.findByTurma_Id(20L))
                .thenReturn(List.of(PresencaStub.entity()));
        when(mapper.toDTO(any()))
                .thenReturn(PresencaStub.dto());

        List<PresencaDTO> lista = service.buscarPorTurma(20L);

        assertEquals(1, lista.size());
        assertEquals(20L, lista.get(0).getTurmaId());
    }

    @Test
    void buscarPorMatricula() {
        when(repository.findByMatricula_Id(10L))
                .thenReturn(List.of(PresencaStub.entity()));
        when(mapper.toDTO(any()))
                .thenReturn(PresencaStub.dto());

        List<PresencaDTO> lista = service.buscarPorMatricula(10L);

        assertEquals(1, lista.size());
        assertEquals(10L, lista.get(0).getMatriculaId());
    }

    @Test
    void criar() {
        when(matriculaRepository.findById(10L))
                .thenReturn(Optional.of(new Matricula()));
        when(turmaRepository.findById(20L))
                .thenReturn(Optional.of(new Turma()));
        when(mapper.toEntity(any()))
                .thenReturn(PresencaStub.nova());
        when(repository.save(any()))
                .thenReturn(PresencaStub.entity());
        when(mapper.toDTO(any()))
                .thenReturn(PresencaStub.dto());

        PresencaDTO criada = service.criar(PresencaStub.dto());

        assertNotNull(criada);
        verify(repository).save(any());
    }

    @Test
    void atualizar() {
        Presenca existente = PresencaStub.entity();
        existente.setPresente(true);

        when(repository.findById(1L))
                .thenReturn(Optional.of(existente));
        when(repository.save(any()))
                .thenReturn(existente);
        when(mapper.toDTO(any()))
                .thenReturn(PresencaStub.dto());

        PresencaDTO atualizada = service.atualizar(1L, PresencaStub.dto().isPresente());

        assertNotNull(atualizada);
        verify(repository).save(any());
    }

    @Test
    void excluir() {
        when(repository.existsById(1L)).thenReturn(true);

        service.excluir(1L);

        verify(repository).deleteById(1L);
    }
}
