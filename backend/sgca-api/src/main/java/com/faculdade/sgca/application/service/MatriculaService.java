package com.faculdade.sgca.application.service;

import com.faculdade.sgca.application.dto.MatriculaDTO;
import com.faculdade.sgca.application.mapper.MatriculaMapper;
import com.faculdade.sgca.domain.model.Aluno;
import com.faculdade.sgca.domain.model.Matricula;
import com.faculdade.sgca.domain.model.Turma;
import com.faculdade.sgca.infrastructure.repository.AlunoRepository;
import com.faculdade.sgca.infrastructure.repository.MatriculaRepository;
import com.faculdade.sgca.infrastructure.repository.TurmaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatriculaService {

    private final MatriculaRepository repository;
    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;
    private final MatriculaMapper mapper;

    // 🔹 LISTAR TODAS
    public List<MatriculaDTO> listarTodas() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    // 🔹 BUSCAR POR ID
    public MatriculaDTO buscarPorId(Long id) {
        Matricula matricula = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Matrícula não encontrada."));
        return mapper.toDTO(matricula);
    }

    // 🔹 BUSCAR POR ALUNO
    public List<MatriculaDTO> buscarPorAluno(Long alunoId) {
        return repository.findByAluno_Id(alunoId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    // 🔹 BUSCAR POR TURMA
    public List<MatriculaDTO> buscarPorTurma(Long turmaId) {
        return repository.findByTurma_Id(turmaId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    // 🔹 CRIAR
    @Transactional
    public MatriculaDTO criar(MatriculaDTO dto) {
        if (repository.existsByAluno_IdAndTurma_Id(dto.getAlunoId(), dto.getTurmaId())) {
            throw new IllegalArgumentException("O aluno já está matriculado nesta turma.");
        }

        Aluno aluno = alunoRepository.findById(dto.getAlunoId())
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado."));
        Turma turma = turmaRepository.findById(dto.getTurmaId())
                .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada."));

        Matricula nova = mapper.toEntity(dto);
        nova.setAluno(aluno);
        nova.setTurma(turma);

        Matricula salva = repository.save(nova);
        return mapper.toDTO(salva);
    }

    // 🔹 ATUALIZAR SITUAÇÃO
    @Transactional
    public MatriculaDTO atualizarSituacao(Long id, String situacao) {
        Matricula matricula = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Matrícula não encontrada."));
        matricula.setSituacao(situacao);
        Matricula atualizada = repository.save(matricula);
        return mapper.toDTO(atualizada);
    }

    // 🔹 EXCLUIR
    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Matrícula não encontrada.");
        }
        repository.deleteById(id);
    }
}
