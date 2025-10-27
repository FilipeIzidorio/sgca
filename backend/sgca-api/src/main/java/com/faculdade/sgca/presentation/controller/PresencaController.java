package com.faculdade.sgca.presentation.controller;

import com.faculdade.sgca.application.dto.PresencaDTO;
import com.faculdade.sgca.application.service.PresencaService;
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
@RequestMapping("/api/v1/presencas")
@RequiredArgsConstructor
@Tag(name = "Presenças", description = "Gerenciamento de presenças (ADMIN e PROFESSOR)")
@SecurityRequirement(name = "bearerAuth")
public class PresencaController {

    private final PresencaService service;

    // 🔹 LISTAR TODAS
    @GetMapping
    @Operation(summary = "Listar todas as presenças")
    public ResponseEntity<List<PresencaDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    // 🔹 BUSCAR POR ID
    @GetMapping("/{id}")
    @Operation(summary = "Buscar presença por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Presença encontrada",
                    content = @Content(schema = @Schema(implementation = PresencaDTO.class))),
            @ApiResponse(responseCode = "404", description = "Presença não encontrada")
    })
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
        }
    }

    // 🔹 BUSCAR POR TURMA
    @GetMapping("/turma/{turmaId}")
    @Operation(summary = "Listar presenças de uma turma específica")
    public ResponseEntity<List<PresencaDTO>> buscarPorTurma(@PathVariable Long turmaId) {
        return ResponseEntity.ok(service.buscarPorTurma(turmaId));
    }

    // 🔹 BUSCAR POR MATRÍCULA
    @GetMapping("/matricula/{matriculaId}")
    @Operation(summary = "Listar presenças de uma matrícula específica")
    public ResponseEntity<List<PresencaDTO>> buscarPorMatricula(@PathVariable Long matriculaId) {
        return ResponseEntity.ok(service.buscarPorMatricula(matriculaId));
    }

    // 🔹 CRIAR
    @PostMapping
    @Operation(summary = "Registrar nova presença")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Presença registrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou duplicados")
    })
    public ResponseEntity<?> criar(@RequestBody PresencaDTO dto) {
        try {
            PresencaDTO criada = service.criar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(criada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // 🔹 ATUALIZAR PRESENÇA
    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar status de presença (presente/ausente)")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        try {
            boolean presente = body.getOrDefault("presente", false);
            PresencaDTO atualizada = service.atualizar(id, presente);
            return ResponseEntity.ok(atualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
        }
    }

    // 🔹 EXCLUIR
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir presença")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Presença excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Presença não encontrada")
    })
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        try {
            service.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
        }
    }
}
