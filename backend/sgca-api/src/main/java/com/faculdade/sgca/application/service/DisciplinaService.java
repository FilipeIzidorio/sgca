package com.faculdade.sgca.application.service;

import com.faculdade.sgca.application.dto.DisciplinaDTO;
import com.faculdade.sgca.application.mapper.DisciplinaMapper;
import com.faculdade.sgca.domain.model.Curso;
import com.faculdade.sgca.domain.model.Disciplina;
import com.faculdade.sgca.infrastructure.repository.CursoRepository;
import com.faculdade.sgca.infrastructure.repository.DisciplinaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DisciplinaService {

    private final DisciplinaRepository repository;
    private final CursoRepository cursoRepository;
    private final DisciplinaMapper mapper;

    // 🔹 LISTAR TODAS
    public List<DisciplinaDTO> listarTodas() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    // 🔹 BUSCAR POR ID
    public DisciplinaDTO buscarPorId(Long id) {
        Disciplina disciplina = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Disciplina não encontrada."));
        return mapper.toDTO(disciplina);
    }

    // 🔹 BUSCAR POR CURSO
    public List<DisciplinaDTO> buscarPorCurso(Long cursoId) {
        return repository.findByCurso_Id(cursoId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    // 🔹 CRIAR
    @Transactional
    public DisciplinaDTO criar(DisciplinaDTO dto) {
        if (repository.existsByCodigo(dto.getCodigo())) {
            throw new IllegalArgumentException("Código de disciplina já cadastrado.");
        }

        Curso curso = cursoRepository.findById(dto.getCursoId())
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado."));

        Disciplina nova = mapper.toEntity(dto);
        nova.setCurso(curso);

        Disciplina salva = repository.save(nova);
        return mapper.toDTO(salva);
    }

    // 🔹 ATUALIZAR
    @Transactional
    public DisciplinaDTO atualizar(Long id, DisciplinaDTO dto) {
        Disciplina disciplina = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Disciplina não encontrada."));

        if (dto.getCodigo() != null && !dto.getCodigo().isBlank()) {
            if (!dto.getCodigo().equalsIgnoreCase(disciplina.getCodigo()) &&
                    repository.existsByCodigo(dto.getCodigo())) {
                throw new IllegalArgumentException("Código de disciplina já cadastrado.");
            }
            disciplina.setCodigo(dto.getCodigo());
        }

        if (dto.getNome() != null && !dto.getNome().isBlank()) {
            disciplina.setNome(dto.getNome());
        }

        if (dto.getCargaHoraria() > 0) {
            disciplina.setCargaHoraria(dto.getCargaHoraria());
        }

        if (dto.getCursoId() != null) {
            Curso curso = cursoRepository.findById(dto.getCursoId())
                    .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado."));
            disciplina.setCurso(curso);
        }

        Disciplina atualizada = repository.save(disciplina);
        return mapper.toDTO(atualizada);
    }

    // 🔹 EXCLUIR
    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Disciplina não encontrada.");
        }
        repository.deleteById(id);
    }
}
