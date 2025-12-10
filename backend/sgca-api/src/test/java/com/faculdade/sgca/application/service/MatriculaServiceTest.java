package com.faculdade.sgca.application.service;

import com.faculdade.sgca.application.dto.MatriculaDTO;
import com.faculdade.sgca.application.mapper.MatriculaMapper;
import com.faculdade.sgca.domain.model.Aluno;
import com.faculdade.sgca.domain.model.Turma;
import com.faculdade.sgca.domain.model.Matricula;
import com.faculdade.sgca.infrastructure.repository.AlunoRepository;
import com.faculdade.sgca.infrastructure.repository.TurmaRepository;
import com.faculdade.sgca.infrastructure.repository.MatriculaRepository;
import stub.MatriculaStub;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatriculaServiceTest {

    @InjectMocks
    private MatriculaService service;

    @Mock
    private MatriculaRepository repository;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private TurmaRepository turmaRepository;

    @Mock
    private MatriculaMapper mapper;

    @Test
    void listarTodas() {
        when(repository.findAll()).thenReturn(List.of(MatriculaStub.entity()));
        when(mapper.toDTO(any())).thenReturn(MatriculaStub.dto());

        List<MatriculaDTO> lista = service.listarTodas();

        assertFalse(lista.isEmpty());
        assertEquals("ATIVA", lista.get(0).getSituacao());
        verify(repository).findAll();
    }

    @Test
    void buscarPorId() {
        when(repository.findById(1L))
                .thenReturn(Optional.of(MatriculaStub.entity()));
        when(mapper.toDTO(any()))
                .thenReturn(MatriculaStub.dto());

        MatriculaDTO dto = service.buscarPorId(1L);

        assertNotNull(dto);
        assertEquals(10L, dto.getAlunoId());
        assertEquals(20L, dto.getTurmaId());
    }

    @Test
    void buscarPorAluno() {
        when(repository.findByAluno_Id(10L))
                .thenReturn(List.of(MatriculaStub.entity()));
        when(mapper.toDTO(any()))
                .thenReturn(MatriculaStub.dto());

        List<MatriculaDTO> lista = service.buscarPorAluno(10L);

        assertEquals(1, lista.size());
        assertEquals("ATIVA", lista.get(0).getSituacao());
    }

    @Test
    void buscarPorTurma() {
        when(repository.findByTurma_Id(20L))
                .thenReturn(List.of(MatriculaStub.entity()));
        when(mapper.toDTO(any()))
                .thenReturn(MatriculaStub.dto());

        List<MatriculaDTO> lista = service.buscarPorTurma(20L);

        assertEquals(1, lista.size());
        assertEquals(20L, lista.get(0).getTurmaId());
    }

    @Test
    void criar() {
        when(alunoRepository.findById(10L))
                .thenReturn(Optional.of(new Aluno()));
        when(turmaRepository.findById(20L))
                .thenReturn(Optional.of(new Turma()));
        when(mapper.toEntity(any()))
                .thenReturn(MatriculaStub.nova());
        when(repository.save(any()))
                .thenReturn(MatriculaStub.entity());
        when(mapper.toDTO(any()))
                .thenReturn(MatriculaStub.dto());

        MatriculaDTO criada = service.criar(MatriculaStub.dto());

        assertNotNull(criada);
        verify(repository).save(any());
    }

    @Test
    void atualizarSituacao() {
        Matricula existente = MatriculaStub.entity();
        existente.setSituacao("ATIVA");

        when(repository.findById(1L))
                .thenReturn(Optional.of(existente));
        when(repository.save(any()))
                .thenReturn(existente);
        when(mapper.toDTO(any()))
                .thenReturn(MatriculaStub.dto());

        MatriculaDTO atualizada = service.atualizarSituacao(1L, "TRANCADA");

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
