package com.faculdade.sgca.application.service;

import com.faculdade.sgca.application.dto.PresencaDTO;
import com.faculdade.sgca.application.mapper.PresencaMapper;
import com.faculdade.sgca.domain.model.Matricula;
import com.faculdade.sgca.domain.model.Presenca;
import com.faculdade.sgca.domain.model.Turma;
import com.faculdade.sgca.infrastructure.repository.MatriculaRepository;
import com.faculdade.sgca.infrastructure.repository.PresencaRepository;
import com.faculdade.sgca.infrastructure.repository.TurmaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PresencaService {

    private final PresencaRepository repository;
    private final TurmaRepository turmaRepository;
    private final MatriculaRepository matriculaRepository;
    private final PresencaMapper mapper;

    // 🔹 LISTAR TODAS
    public List<PresencaDTO> listarTodas() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    // 🔹 BUSCAR POR ID
    public PresencaDTO buscarPorId(Long id) {
        Presenca presenca = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Presença não encontrada."));
        return mapper.toDTO(presenca);
    }

    // 🔹 BUSCAR POR TURMA
    public List<PresencaDTO> buscarPorTurma(Long turmaId) {
        return repository.findByTurma_Id(turmaId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    // 🔹 BUSCAR POR MATRÍCULA
    public List<PresencaDTO> buscarPorMatricula(Long matriculaId) {
        return repository.findByMatricula_Id(matriculaId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    // 🔹 CRIAR
    @Transactional
    public PresencaDTO criar(PresencaDTO dto) {
        if (repository.existsByTurma_IdAndMatricula_IdAndDataAula(dto.getTurmaId(), dto.getMatriculaId(), dto.getDataAula())) {
            throw new IllegalArgumentException("Já existe uma presença registrada para esta data, turma e matrícula.");
        }

        Turma turma = turmaRepository.findById(dto.getTurmaId())
                .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada."));
        Matricula matricula = matriculaRepository.findById(dto.getMatriculaId())
                .orElseThrow(() -> new IllegalArgumentException("Matrícula não encontrada."));

        Presenca nova = mapper.toEntity(dto);
        nova.setTurma(turma);
        nova.setMatricula(matricula);
        nova.setPresente(dto.isPresente());

        Presenca salva = repository.save(nova);
        return mapper.toDTO(salva);
    }

    // 🔹 ATUALIZAR PRESENÇA
    @Transactional
    public PresencaDTO atualizar(Long id, boolean presente) {
        Presenca presenca = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Presença não encontrada."));
        presenca.setPresente(presente);
        Presenca atualizada = repository.save(presenca);
        return mapper.toDTO(atualizada);
    }

    // 🔹 EXCLUIR
    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Presença não encontrada.");
        }
        repository.deleteById(id);
    }
}
