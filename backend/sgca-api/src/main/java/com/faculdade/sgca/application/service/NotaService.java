package com.faculdade.sgca.application.service;

import com.faculdade.sgca.application.dto.NotaDTO;
import com.faculdade.sgca.application.mapper.NotaMapper;
import com.faculdade.sgca.domain.model.Avaliacao;
import com.faculdade.sgca.domain.model.Matricula;
import com.faculdade.sgca.domain.model.Nota;
import com.faculdade.sgca.infrastructure.repository.AvaliacaoRepository;
import com.faculdade.sgca.infrastructure.repository.MatriculaRepository;
import com.faculdade.sgca.infrastructure.repository.NotaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotaService {

    private final NotaRepository repository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final MatriculaRepository matriculaRepository;
    private final NotaMapper mapper;

    // 🔹 LISTAR TODAS
    public List<NotaDTO> listarTodas() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    // 🔹 BUSCAR POR ID
    public NotaDTO buscarPorId(Long id) {
        Nota nota = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nota não encontrada."));
        return mapper.toDTO(nota);
    }

    // 🔹 BUSCAR POR MATRÍCULA
    public List<NotaDTO> buscarPorMatricula(Long matriculaId) {
        return repository.findByMatricula_Id(matriculaId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    // 🔹 BUSCAR POR TURMA
    public List<NotaDTO> buscarPorTurma(Long turmaId) {
        return repository.findByAvaliacao_TurmaId(turmaId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    // 🔹 CRIAR
    @Transactional
    public NotaDTO criar(NotaDTO dto) {
        if (repository.existsByAvaliacao_IdAndMatricula_Id(dto.getAvaliacaoId(), dto.getMatriculaId())) {
            throw new IllegalArgumentException("Já existe nota registrada para essa avaliação e matrícula.");
        }

        Avaliacao avaliacao = avaliacaoRepository.findById(dto.getAvaliacaoId())
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada."));

        Matricula matricula = matriculaRepository.findById(dto.getMatriculaId())
                .orElseThrow(() -> new IllegalArgumentException("Matrícula não encontrada."));

        Nota nova = mapper.toEntity(dto);
        nova.setAvaliacao(avaliacao);
        nova.setMatricula(matricula);

        Nota salva = repository.save(nova);
        return mapper.toDTO(salva);
    }

    // 🔹 ATUALIZAR VALOR
    @Transactional
    public NotaDTO atualizarValor(Long id, double novoValor) {
        if (novoValor < 0 || novoValor > 10) {
            throw new IllegalArgumentException("A nota deve estar entre 0 e 10.");
        }

        Nota nota = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nota não encontrada."));

        nota.setValor(novoValor);
        Nota atualizada = repository.save(nota);

        return mapper.toDTO(atualizada);
    }

    // 🔹 EXCLUIR
    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Nota não encontrada.");
        }
        repository.deleteById(id);
    }
}
