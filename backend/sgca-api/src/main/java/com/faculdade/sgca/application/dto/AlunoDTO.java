package com.faculdade.sgca.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para transferência de dados de Aluno")
public class AlunoDTO {

    @Schema(description = "Identificador único do aluno", example = "1")
    private Long id;

    @Schema(description = "Nome completo do aluno", example = "Maria Oliveira")
    private String nome;

    @Schema(description = "E-mail institucional ou pessoal do aluno", example = "maria.oliveira@example.com")
    private String email;

    @Schema(description = "CPF do aluno", example = "123.456.789-00")
    private String cpf;

    @Schema(description = "Data de nascimento do aluno", example = "2000-05-15")
    private LocalDate dataNascimento;

    @Schema(description = "Status acadêmico do aluno (ATIVO, INATIVO, FORMADO)", example = "ATIVO")
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
