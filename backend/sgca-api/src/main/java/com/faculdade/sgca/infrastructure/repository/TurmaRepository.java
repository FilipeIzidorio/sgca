package com.faculdade.sgca.infrastructure.repository;

import com.faculdade.sgca.domain.model.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long> {
    List<Turma> findByPeriodo(String periodo);
    List<Turma> findByDisciplina_Id(Long disciplinaId);
    List<Turma> findByProfessor_Id(Long professorId);
    boolean existsByDisciplina_IdAndPeriodo(Long disciplinaId, String periodo);
}
