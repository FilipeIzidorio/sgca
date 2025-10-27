package com.faculdade.sgca.application.service;

import com.faculdade.sgca.application.dto.TurmaDTO;
import com.faculdade.sgca.application.mapper.TurmaMapper;
import com.faculdade.sgca.domain.model.Disciplina;
import com.faculdade.sgca.domain.model.Turma;
import com.faculdade.sgca.domain.model.Usuario;
import com.faculdade.sgca.infrastructure.repository.DisciplinaRepository;
import com.faculdade.sgca.infrastructure.repository.TurmaRepository;
import com.faculdade.sgca.infrastructure.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TurmaService {

    private final TurmaRepository repository;
    private final DisciplinaRepository disciplinaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TurmaMapper mapper;

    // 🔹 LISTAR TODAS
    public List<TurmaDTO> listarTodas() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    // 🔹 BUSCAR POR ID
    public TurmaDTO buscarPorId(Long id) {
        Turma turma = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada."));
        return mapper.toDTO(turma);
    }

    // 🔹 BUSCAR POR DISCIPLINA
    public List<TurmaDTO> buscarPorDisciplina(Long disciplinaId) {
        return repository.findByDisciplina_Id(disciplinaId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    // 🔹 BUSCAR POR PROFESSOR
    public List<TurmaDTO> buscarPorProfessor(Long professorId) {
        return repository.findByProfessor_Id(professorId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    // 🔹 BUSCAR POR PERÍODO
    public List<TurmaDTO> buscarPorPeriodo(String periodo) {
        return repository.findByPeriodo(periodo)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    // 🔹 CRIAR
    @Transactional
    public TurmaDTO criar(TurmaDTO dto) {
        // 🧩 Validação básica
        if (dto.getDisciplinaId() == null) {
            throw new IllegalArgumentException("O campo 'disciplinaId' é obrigatório.");
        }
        if (dto.getPeriodo() == null || dto.getPeriodo().isBlank()) {
            throw new IllegalArgumentException("O campo 'periodo' é obrigatório.");
        }

        // 🧩 Verifica duplicidade (disciplina + período)
        if (repository.existsByDisciplina_IdAndPeriodo(dto.getDisciplinaId(), dto.getPeriodo())) {
            throw new IllegalArgumentException("Já existe uma turma para esta disciplina e período.");
        }

        // 🧩 Busca disciplina
        Disciplina disciplina = disciplinaRepository.findById(dto.getDisciplinaId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Disciplina não encontrada com ID: " + dto.getDisciplinaId()));

        // 🧩 Busca professor, se informado
        Usuario professor = null;
        if (dto.getProfessorId() != null) {
            professor = usuarioRepository.findById(dto.getProfessorId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Professor não encontrado com ID: " + dto.getProfessorId()));
        }

        // 🧩 Cria entidade da turma
        Turma nova = new Turma();
        nova.setDisciplina(disciplina);
        nova.setProfessor(professor);
        nova.setPeriodo(dto.getPeriodo());
        nova.setCapacidade(dto.getCapacidade());

        // 🧩 Salva no banco
        Turma salva = repository.save(nova);

        // 🧩 Retorna DTO com dados atualizados
        return mapper.toDTO(salva);
    }

    // 🔹 ATUALIZAR
    @Transactional
    public TurmaDTO atualizar(Long id, TurmaDTO dto) {
        Turma turma = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada."));

        if (dto.getPeriodo() != null) turma.setPeriodo(dto.getPeriodo());
        if (dto.getCapacidade() != null) turma.setCapacidade(dto.getCapacidade());

        if (dto.getProfessorId() != null) {
            Usuario professor = usuarioRepository.findById(dto.getProfessorId())
                    .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado."));
            turma.setProfessor(professor);
        }

        Turma atualizada = repository.save(turma);
        return mapper.toDTO(atualizada);
    }

    // 🔹 EXCLUIR
    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Turma não encontrada.");
        }
        repository.deleteById(id);
    }
}
