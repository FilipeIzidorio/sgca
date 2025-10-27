package com.faculdade.sgca.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para transferência de dados de Avaliação")
public class AvaliacaoDTO {

    @Schema(description = "Identificador único da avaliação", example = "1")
    private Long id;

    @Schema(description = "Título da avaliação", example = "Prova Bimestral")
    private String titulo;

    @Schema(description = "Peso da avaliação (0–100)", example = "40.0")
    private double peso;

    @Schema(description = "Tipo de avaliação (Prova, Trabalho, Participacao)", example = "PROVA")
    private String tipo;

    @Schema(description = "Identificador da turma associada", example = "10")
    private Long turmaId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getTurmaId() {
        return turmaId;
    }

    public void setTurmaId(Long turmaId) {
        this.turmaId = turmaId;
    }
}
