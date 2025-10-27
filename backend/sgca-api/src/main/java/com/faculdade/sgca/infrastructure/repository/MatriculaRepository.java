package com.faculdade.sgca.infrastructure.repository;

import com.faculdade.sgca.domain.model.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
    Optional<Matricula> findByAluno_IdAndTurma_Id(Long alunoId, Long turmaId);
    List<Matricula> findByAluno_Id(Long alunoId);
    List<Matricula> findByTurma_Id(Long turmaId);
    boolean existsByAluno_IdAndTurma_Id(Long alunoId, Long turmaId);
}
