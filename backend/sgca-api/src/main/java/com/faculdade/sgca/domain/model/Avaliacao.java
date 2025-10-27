package com.faculdade.sgca.domain.model;


import com.faculdade.sgca.domain.avaliacao.TipoAvaliacao;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "avaliacao")
@NoArgsConstructor
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private double peso;

    @Column(nullable = false)
    private String tipo; // Prova, Trabalho, Participacao

    @Column(nullable = false)
    private Long turmaId;

    @Transient
    private TipoAvaliacao implementacao;

    public Avaliacao(String titulo, double peso, String tipo, Long turmaId, TipoAvaliacao implementacao) {
        this.titulo = titulo;
        this.peso = peso;
        this.tipo = tipo;
        this.turmaId = turmaId;
        this.implementacao = implementacao;
    }

    public double calcularNotaFinal(double valorObtido) {
        if (implementacao == null)
            throw new IllegalStateException("Tipo de avaliação não definido.");
        return implementacao.calcularNotaFinal(valorObtido, peso);
    }

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

    public TipoAvaliacao getImplementacao() {
        return implementacao;
    }

    public void setImplementacao(TipoAvaliacao implementacao) {
        this.implementacao = implementacao;
    }
}