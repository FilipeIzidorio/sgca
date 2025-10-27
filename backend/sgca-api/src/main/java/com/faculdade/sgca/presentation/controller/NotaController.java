package com.faculdade.sgca.presentation.controller;

import com.faculdade.sgca.application.dto.NotaDTO;
import com.faculdade.sgca.application.service.NotaService;
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
@RequestMapping("/api/v1/notas")
@RequiredArgsConstructor
@Tag(name = "Notas", description = "Gerenciamento de notas dos alunos (ADMIN e PROFESSOR)")
@SecurityRequirement(name = "bearerAuth")
public class NotaController {

    private final NotaService service;

    // 🔹 LISTAR TODAS
    @GetMapping
    @Operation(summary = "Listar todas as notas")
    public ResponseEntity<List<NotaDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    // 🔹 BUSCAR POR ID
    @GetMapping("/{id}")
    @Operation(summary = "Buscar nota por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nota encontrada",
                    content = @Content(schema = @Schema(implementation = NotaDTO.class))),
            @ApiResponse(responseCode = "404", description = "Nota não encontrada")
    })
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // 🔹 BUSCAR POR MATRÍCULA
    @GetMapping("/matricula/{matriculaId}")
    @Operation(summary = "Listar notas de uma matrícula específica")
    public ResponseEntity<List<NotaDTO>> buscarPorMatricula(@PathVariable Long matriculaId) {
        return ResponseEntity.ok(service.buscarPorMatricula(matriculaId));
    }

    // 🔹 BUSCAR POR TURMA
    @GetMapping("/turma/{turmaId}")
    @Operation(summary = "Listar notas de uma turma específica")
    public ResponseEntity<List<NotaDTO>> buscarPorTurma(@PathVariable Long turmaId) {
        return ResponseEntity.ok(service.buscarPorTurma(turmaId));
    }

    // 🔹 CRIAR
    @PostMapping
    @Operation(summary = "Cadastrar nova nota")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Nota cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou duplicados")
    })
    public ResponseEntity<?> criar(@RequestBody NotaDTO dto) {
        try {
            NotaDTO criada = service.criar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(criada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // 🔹 ATUALIZAR VALOR
    @PatchMapping("/{id}/valor")
    @Operation(summary = "Atualizar valor da nota")
    public ResponseEntity<?> atualizarValor(@PathVariable Long id, @RequestBody Map<String, Double> body) {
        try {
            double novoValor = body.getOrDefault("valor", -1.0);
            NotaDTO atualizada = service.atualizarValor(id, novoValor);
            return ResponseEntity.ok(atualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // 🔹 EXCLUIR
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir nota")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Nota excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nota não encontrada")
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
