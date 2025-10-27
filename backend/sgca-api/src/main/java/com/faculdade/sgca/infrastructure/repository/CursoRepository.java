package com.faculdade.sgca.infrastructure.repository;

import com.faculdade.sgca.domain.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    Optional<Curso> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
}
