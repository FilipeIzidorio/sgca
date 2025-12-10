package com.faculdade.sgca.application.service;

import com.faculdade.sgca.application.dto.NotaDTO;
import com.faculdade.sgca.application.mapper.NotaMapper;
import com.faculdade.sgca.domain.model.Matricula;
import com.faculdade.sgca.domain.model.Nota;
import com.faculdade.sgca.domain.model.Turma;
import com.faculdade.sgca.infrastructure.repository.MatriculaRepository;
import com.faculdade.sgca.infrastructure.repository.NotaRepository;
import com.faculdade.sgca.infrastructure.repository.TurmaRepository;
import stub.NotaStub;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotaServiceTest {

    @InjectMocks
    private NotaService service;

    @Mock
    private NotaRepository repository;

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private TurmaRepository turmaRepository;

    @Mock
    private NotaMapper mapper;

    @Test
    void listarTodas() {
        when(repository.findAll()).thenReturn(List.of(NotaStub.entity()));
        when(mapper.toDTO(any())).thenReturn(NotaStub.dto());

        List<NotaDTO> lista = service.listarTodas();

        assertFalse(lista.isEmpty());
        assertEquals(8.5, lista.get(0).getValor());
    }

    @Test
    void buscarPorId() {
        when(repository.findById(1L))
                .thenReturn(Optional.of(NotaStub.entity()));
        when(mapper.toDTO(any()))
                .thenReturn(NotaStub.dto());

        NotaDTO dto = service.buscarPorId(1L);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
    }

    @Test
    void buscarPorMatricula() {
        when(repository.findByMatricula_Id(10L))
                .thenReturn(List.of(NotaStub.entity()));
        when(mapper.toDTO(any()))
                .thenReturn(NotaStub.dto());

        List<NotaDTO> lista = service.buscarPorMatricula(10L);

        assertEquals(1, lista.size());
        assertEquals(10L, lista.get(0).getMatriculaId());
    }

    @Test
    void buscarPorTurma() {
        when(repository.findByAvaliacao_TurmaId(20L))
                .thenReturn(List.of(NotaStub.entity()));
        when(mapper.toDTO(any()))
                .thenReturn(NotaStub.dto());

        List<NotaDTO> lista = service.buscarPorTurma(20L);

        assertEquals(1, lista.size());

    }

    @Test
    void criar() {
        when(matriculaRepository.findById(10L))
                .thenReturn(Optional.of(new Matricula()));
        when(turmaRepository.findById(20L))
                .thenReturn(Optional.of(new Turma()));
        when(mapper.toEntity(any()))
                .thenReturn(NotaStub.nova());
        when(repository.save(any()))
                .thenReturn(NotaStub.entity());
        when(mapper.toDTO(any()))
                .thenReturn(NotaStub.dto());

        NotaDTO criada = service.criar(NotaStub.dto());

        assertNotNull(criada);
        verify(repository).save(any());
    }

    @Test
    void atualizarValor() {
        Nota existente = NotaStub.entity();

        when(repository.findById(1L))
                .thenReturn(Optional.of(existente));
        when(repository.save(any()))
                .thenReturn(existente);
        when(mapper.toDTO(any()))
                .thenReturn(NotaStub.dto());

        NotaDTO atualizada = service.atualizarValor(1L, 9.0);

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
