package com.faculdade.sgca.infrastructure.repository;

import com.faculdade.sgca.domain.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Long> {
    Optional<Nota> findByAvaliacao_IdAndMatricula_Id(Long avaliacaoId, Long matriculaId);
    List<Nota> findByMatricula_Id(Long matriculaId);
    List<Nota> findByAvaliacao_TurmaId(Long turmaId);
    boolean existsByAvaliacao_IdAndMatricula_Id(Long avaliacaoId, Long matriculaId);
}
