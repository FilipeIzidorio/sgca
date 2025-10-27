package com.faculdade.sgca.application.service;

import com.faculdade.sgca.application.dto.AvaliacaoDTO;
import com.faculdade.sgca.application.mapper.AvaliacaoMapper;
import com.faculdade.sgca.domain.avaliacao.TipoAvaliacao;
import com.faculdade.sgca.domain.avaliacao.impl.Participacao;
import com.faculdade.sgca.domain.avaliacao.impl.Prova;
import com.faculdade.sgca.domain.avaliacao.impl.Trabalho;
import com.faculdade.sgca.domain.model.Avaliacao;
import com.faculdade.sgca.infrastructure.repository.AvaliacaoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private final AvaliacaoRepository repository;
    private final AvaliacaoMapper mapper;

    // ============================================
    // LISTAR TODAS AS AVALIAÇÕES
    // ============================================
    public List<AvaliacaoDTO> listar() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    // ============================================
    // BUSCAR POR ID
    // ============================================
    public AvaliacaoDTO buscarPorId(Long id) {
        Avaliacao entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada."));
        return mapper.toDTO(entity);
    }

    // ============================================
    // CRIAR NOVA AVALIAÇÃO
    // ============================================
    @Transactional
    public AvaliacaoDTO criar(AvaliacaoDTO dto) {
        validarCampos(dto);

        List<Avaliacao> existentes = repository.findByTurmaId(dto.getTurmaId());
        double somaPesos = existentes.stream()
                .mapToDouble(Avaliacao::getPeso)
                .sum();

        if (somaPesos + dto.getPeso() > 100.0) {
            throw new RuntimeException("A soma dos pesos das avaliações da turma ultrapassa 100%.");
        }

        Avaliacao entity = mapper.toEntity(dto);
        entity.setImplementacao(getImplementacao(dto.getTipo()));

        Avaliacao salvo = repository.save(entity);
        return mapper.toDTO(salvo);
    }

    // ============================================
    // ATUALIZAR AVALIAÇÃO
    // ============================================
    @Transactional
    public AvaliacaoDTO atualizar(Long id, AvaliacaoDTO dto) {
        Avaliacao existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada."));

        validarCampos(dto);

        existente.setTitulo(dto.getTitulo());
        existente.setPeso(dto.getPeso());
        existente.setTipo(dto.getTipo());
        existente.setTurmaId(dto.getTurmaId());
        existente.setImplementacao(getImplementacao(dto.getTipo()));

        Avaliacao atualizado = repository.save(existente);
        return mapper.toDTO(atualizado);
    }

    // ============================================
    // EXCLUIR
    // ============================================
    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Avaliação não encontrada.");
        }
        repository.deleteById(id);
    }

    // ============================================
    // MÉTODOS AUXILIARES
    // ============================================
    private void validarCampos(AvaliacaoDTO dto) {
        if (dto.getTitulo() == null || dto.getTitulo().isBlank()) {
            throw new RuntimeException("O campo 'título' é obrigatório.");
        }
        if (dto.getPeso() <= 0 || dto.getPeso() > 100) {
            throw new RuntimeException("O campo 'peso' deve ser maior que 0 e menor ou igual a 100.");
        }
        if (dto.getTipo() == null || dto.getTipo().isBlank()) {
            throw new RuntimeException("O campo 'tipo' é obrigatório.");
        }
        if (dto.getTurmaId() == null) {
            throw new RuntimeException("O campo 'turmaId' é obrigatório.");
        }
    }

    private TipoAvaliacao getImplementacao(String tipo) {
        return switch (tipo.toUpperCase()) {
            case "PROVA" -> new Prova();
            case "TRABALHO" -> new Trabalho();
            case "PARTICIPACAO" -> new Participacao();
            default -> throw new RuntimeException("Tipo de avaliação inválido: " + tipo);
        };
    }
}
