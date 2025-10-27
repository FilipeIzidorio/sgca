package com.faculdade.sgca.application.service;

import com.faculdade.sgca.application.dto.AlunoDTO;
import com.faculdade.sgca.application.mapper.AlunoMapper;
import com.faculdade.sgca.domain.model.Aluno;
import com.faculdade.sgca.infrastructure.repository.AlunoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository repository;
    private final AlunoMapper mapper;

    // 🔹 LISTAR TODOS
    public List<AlunoDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    // 🔹 BUSCAR POR ID
    public AlunoDTO buscarPorId(Long id) {
        Aluno aluno = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado."));
        return mapper.toDTO(aluno);
    }

    // 🔹 CRIAR NOVO ALUNO
    @Transactional
    public AlunoDTO criar(AlunoDTO dto) {
        if (repository.existsByCpf(dto.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado.");
        }
        if (repository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }

        Aluno novo = mapper.toEntity(dto);
        Aluno salvo = repository.save(novo);
        return mapper.toDTO(salvo);
    }

    // 🔹 ATUALIZAR
    @Transactional
    public AlunoDTO atualizar(Long id, AlunoDTO dto) {
        Aluno aluno = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado."));

        if (dto.getNome() != null && !dto.getNome().isBlank()) {
            aluno.setNome(dto.getNome());
        }

        if (dto.getEmail() != null && !dto.getEmail().equalsIgnoreCase(aluno.getEmail())) {
            if (repository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("E-mail já cadastrado: " + dto.getEmail());
            }
            aluno.setEmail(dto.getEmail());
        }

        if (dto.getCpf() != null && !dto.getCpf().equals(aluno.getCpf())) {
            if (repository.existsByCpf(dto.getCpf())) {
                throw new IllegalArgumentException("CPF já cadastrado: " + dto.getCpf());
            }
            aluno.setCpf(dto.getCpf());
        }

        if (dto.getDataNascimento() != null) {
            aluno.setDataNascimento(dto.getDataNascimento());
        }

        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            aluno.setStatus(dto.getStatus());
        }

        Aluno atualizado = repository.save(aluno);
        return mapper.toDTO(atualizado);
    }

    // 🔹 EXCLUIR
    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Aluno não encontrado.");
        }
        repository.deleteById(id);
    }
}
