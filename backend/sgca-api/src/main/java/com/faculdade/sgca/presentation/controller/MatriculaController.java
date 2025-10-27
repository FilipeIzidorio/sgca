package com.faculdade.sgca.presentation.controller;

import com.faculdade.sgca.application.dto.MatriculaDTO;
import com.faculdade.sgca.application.service.MatriculaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/matriculas")
@RequiredArgsConstructor
@Tag(name = "Matrículas", description = "Gerenciamento de matrículas (ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class MatriculaController {

    private final MatriculaService service;

    // 🔹 LISTAR TODAS
    @GetMapping
    @Operation(summary = "Listar todas as matrículas")
    public ResponseEntity<List<MatriculaDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    // 🔹 BUSCAR POR ID
    @GetMapping("/{id}")
    @Operation(summary = "Buscar matrícula por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Matrícula encontrada",
                    content = @Content(schema = @Schema(implementation = MatriculaDTO.class))),
            @ApiResponse(responseCode = "404", description = "Matrícula não encontrada")
    })
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // 🔹 BUSCAR POR ALUNO
    @GetMapping("/aluno/{alunoId}")
    @Operation(summary = "Listar matrículas de um aluno específico")
    public ResponseEntity<List<MatriculaDTO>> buscarPorAluno(@PathVariable Long alunoId) {
        return ResponseEntity.ok(service.buscarPorAluno(alunoId));
    }

    // 🔹 BUSCAR POR TURMA
    @GetMapping("/turma/{turmaId}")
    @Operation(summary = "Listar matrículas de uma turma específica")
    public ResponseEntity<List<MatriculaDTO>> buscarPorTurma(@PathVariable Long turmaId) {
        return ResponseEntity.ok(service.buscarPorTurma(turmaId));
    }

    // 🔹 CRIAR MATRÍCULA
    @PostMapping
    @Operation(summary = "Cadastrar nova matrícula")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Matrícula criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou duplicados")
    })
    public ResponseEntity<?> criar(@RequestBody MatriculaDTO dto) {
        try {
            MatriculaDTO criada = service.criar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(criada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // 🔹 ATUALIZAR SITUAÇÃO
    @PatchMapping("/{id}/situacao")
    @Operation(summary = "Atualizar situação da matrícula (ATIVA, TRANCADA, CANCELADA)")
    public ResponseEntity<?> atualizarSituacao(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String situacao = body.get("situacao");
            MatriculaDTO atualizada = service.atualizarSituacao(id, situacao);
            return ResponseEntity.ok(atualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // 🔹 EXCLUIR
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir matrícula")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Matrícula excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Matrícula não encontrada")
    })
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        try {
            service.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }
}
