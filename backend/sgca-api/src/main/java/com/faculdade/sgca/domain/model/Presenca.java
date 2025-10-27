    package com.faculdade.sgca.domain.model;

    import jakarta.persistence.*;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import java.time.LocalDate;

    @Entity
    @Table(
            name = "presenca",
            uniqueConstraints = @UniqueConstraint(columnNames = {"turma_id", "matricula_id", "data_aula"})
    )

    @NoArgsConstructor
    public class Presenca {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(optional = false)
        @JoinColumn(name = "turma_id", nullable = false)
        private Turma turma;

        @ManyToOne(optional = false)
        @JoinColumn(name = "matricula_id", nullable = false)
        private Matricula matricula;

        @Column(name = "data_aula", nullable = false)
        private LocalDate dataAula;

        @Column(nullable = false)
        private boolean presente;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Turma getTurma() {
            return turma;
        }

        public void setTurma(Turma turma) {
            this.turma = turma;
        }

        public Matricula getMatricula() {
            return matricula;
        }

        public void setMatricula(Matricula matricula) {
            this.matricula = matricula;
        }

        public LocalDate getDataAula() {
            return dataAula;
        }

        public void setDataAula(LocalDate dataAula) {
            this.dataAula = dataAula;
        }

        public boolean isPresente() {
            return presente;
        }

        public void setPresente(boolean presente) {
            this.presente = presente;
        }
    }
