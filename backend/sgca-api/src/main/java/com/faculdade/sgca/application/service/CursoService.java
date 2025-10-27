package com.faculdade.sgca.application.service;

import com.faculdade.sgca.application.dto.CursoDTO;
import com.faculdade.sgca.application.mapper.CursoMapper;
import com.faculdade.sgca.domain.model.Curso;
import com.faculdade.sgca.infrastructure.repository.CursoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository repository;
    private final CursoMapper mapper;

    // 🔹 LISTAR TODOS
    public List<CursoDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    // 🔹 BUSCAR POR ID
    public CursoDTO buscarPorId(Long id) {
        Curso curso = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado."));
        return mapper.toDTO(curso);
    }

    // 🔹 CRIAR NOVO CURSO
    @Transactional
    public CursoDTO criar(CursoDTO dto) {
        if (repository.existsByCodigo(dto.getCodigo())) {
            throw new IllegalArgumentException("Código de curso já cadastrado.");
        }

        Curso novo = mapper.toEntity(dto);
        Curso salvo = repository.save(novo);
        return mapper.toDTO(salvo);
    }

    // 🔹 ATUALIZAR CURSO
    @Transactional
    public CursoDTO atualizar(Long id, CursoDTO dto) {
        Curso curso = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado."));

        if (dto.getCodigo() != null && !dto.getCodigo().isBlank()) {
            if (!dto.getCodigo().equalsIgnoreCase(curso.getCodigo())
                    && repository.existsByCodigo(dto.getCodigo())) {
                throw new IllegalArgumentException("Código de curso já cadastrado: " + dto.getCodigo());
            }
            curso.setCodigo(dto.getCodigo());
        }

        if (dto.getNome() != null && !dto.getNome().isBlank()) {
            curso.setNome(dto.getNome());
        }

        if (dto.getDescricao() != null) {
            curso.setDescricao(dto.getDescricao());
        }

        if (dto.getCargaHoraria() > 0) {
            curso.setCargaHoraria(dto.getCargaHoraria());
        }

        Curso atualizado = repository.save(curso);
        return mapper.toDTO(atualizado);
    }

    // 🔹 EXCLUIR
    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Curso não encontrado.");
        }
        repository.deleteById(id);
    }
}
