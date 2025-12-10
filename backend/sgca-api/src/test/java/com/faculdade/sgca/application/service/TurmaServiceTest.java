package com.faculdade.sgca.application.service;

import com.faculdade.sgca.application.dto.TurmaDTO;
import com.faculdade.sgca.application.mapper.TurmaMapper;
import com.faculdade.sgca.domain.model.Disciplina;
import com.faculdade.sgca.domain.model.Usuario;
import com.faculdade.sgca.domain.model.Turma;
import com.faculdade.sgca.infrastructure.repository.DisciplinaRepository;
import com.faculdade.sgca.infrastructure.repository.UsuarioRepository;
import com.faculdade.sgca.infrastructure.repository.TurmaRepository;
import stub.TurmaStub;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TurmaServiceTest {

    @InjectMocks
    private TurmaService service;

    @Mock
    private TurmaRepository repository;

    @Mock
    private DisciplinaRepository disciplinaRepository;

    @Mock
    private UsuarioRepository professorRepository;

    @Mock
    private TurmaMapper mapper;

    @Test
    void listarTodas() {
        when(repository.findAll()).thenReturn(List.of(TurmaStub.entity()));
        when(mapper.toDTO(any())).thenReturn(TurmaStub.dto());

        List<TurmaDTO> lista = service.listarTodas();

        assertFalse(lista.isEmpty());
        assertEquals("2025.1", lista.get(0).getPeriodo());
    }

    @Test
    void buscarPorId() {
        when(repository.findById(1L))
                .thenReturn(Optional.of(TurmaStub.entity()));
        when(mapper.toDTO(any()))
                .thenReturn(TurmaStub.dto());

        TurmaDTO dto = service.buscarPorId(1L);

        assertNotNull(dto);
        assertEquals(10L, dto.getDisciplinaId());
    }

    @Test
    void buscarPorDisciplina() {
        when(repository.findByDisciplina_Id(10L))
                .thenReturn(List.of(TurmaStub.entity()));
        when(mapper.toDTO(any()))
                .thenReturn(TurmaStub.dto());

        List<TurmaDTO> lista = service.buscarPorDisciplina(10L);

        assertEquals(1, lista.size());
        assertEquals(10L, lista.get(0).getDisciplinaId());
    }

    @Test
    void buscarPorProfessor() {
        when(repository.findByProfessor_Id(20L))
                .thenReturn(List.of(TurmaStub.entity()));
        when(mapper.toDTO(any()))
                .thenReturn(TurmaStub.dto());

        List<TurmaDTO> lista = service.buscarPorProfessor(20L);

        assertEquals(1, lista.size());
        assertEquals(20L, lista.get(0).getProfessorId());
    }

    @Test
    void buscarPorPeriodo() {
        when(repository.findByPeriodo("2025.1"))
                .thenReturn(List.of(TurmaStub.entity()));
        when(mapper.toDTO(any()))
                .thenReturn(TurmaStub.dto());

        List<TurmaDTO> lista = service.buscarPorPeriodo("2025.1");

        assertEquals(1, lista.size());
        assertEquals("2025.1", lista.get(0).getPeriodo());
    }

    @Test
    void criar() {
        when(disciplinaRepository.findById(10L))
                .thenReturn(Optional.of(new Disciplina()));
        when(professorRepository.findById(20L))
                .thenReturn(Optional.of(new Usuario()));
        when(mapper.toEntity(any()))
                .thenReturn(TurmaStub.nova());
        when(repository.save(any()))
                .thenReturn(TurmaStub.entity());
        when(mapper.toDTO(any()))
                .thenReturn(TurmaStub.dto());

        TurmaDTO criada = service.criar(TurmaStub.dto());

        assertNotNull(criada);
        verify(repository).save(any());
    }

    @Test
    void atualizar() {
        Turma existente = TurmaStub.entity();

        when(repository.findById(1L))
                .thenReturn(Optional.of(existente));
        when(repository.save(any()))
                .thenReturn(existente);
        when(mapper.toDTO(any()))
                .thenReturn(TurmaStub.dto());

        TurmaDTO atualizada = service.atualizar(1L, TurmaStub.dto());

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
