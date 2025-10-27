package com.faculdade.sgca.infrastructure.repository;

import com.faculdade.sgca.domain.model.Presenca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PresencaRepository extends JpaRepository<Presenca, Long> {
    Optional<Presenca> findByTurma_IdAndMatricula_IdAndDataAula(Long turmaId, Long matriculaId, LocalDate dataAula);
    List<Presenca> findByTurma_Id(Long turmaId);
    List<Presenca> findByMatricula_Id(Long matriculaId);
    boolean existsByTurma_IdAndMatricula_IdAndDataAula(Long turmaId, Long matriculaId, LocalDate dataAula);
}
