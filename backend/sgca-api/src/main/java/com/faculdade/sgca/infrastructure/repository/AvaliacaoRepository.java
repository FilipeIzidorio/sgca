package com.faculdade.sgca.infrastructure.repository;

import com.faculdade.sgca.domain.model.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    /**
     * Retorna todas as avaliações associadas a uma turma específica.
     * Usado para validar soma dos pesos (não pode ultrapassar 100%).
     */
    List<Avaliacao> findByTurmaId(Long turmaId);

    /**
     * Retorna a soma total dos pesos das avaliações de uma turma.
     * Usado para validação antes de criar nova avaliação.
     */
    @Query("SELECT COALESCE(SUM(a.peso), 0) FROM Avaliacao a WHERE a.turmaId = :turmaId")
    double somaPesosPorTurma(Long turmaId);

    /**
     * Retorna todas as avaliações de um tipo específico (Prova, Trabalho, Participação).
     * Pode ser útil para relatórios e Bridge extensions.
     */
    List<Avaliacao> findByTipoIgnoreCase(String tipo);
}
