package com.faculdade.sgca.presentation.controller;

import com.faculdade.sgca.application.dto.TurmaDTO;
import com.faculdade.sgca.application.service.TurmaService;
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
@RequestMapping("/api/v1/turmas")
@RequiredArgsConstructor
@Tag(name = "Turmas", description = "Gerenciamento de turmas (ADMIN e PROFESSOR)")
@SecurityRequirement(name = "bearerAuth")
public class TurmaController {

    private final TurmaService service;

    // 🔹 LISTAR TODAS
    @GetMapping
    @Operation(summary = "Listar todas as turmas")
    public ResponseEntity<List<TurmaDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    // 🔹 BUSCAR POR ID
    @GetMapping("/{id}")
    @Operation(summary = "Buscar turma por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Turma encontrada",
                    content = @Content(schema = @Schema(implementation = TurmaDTO.class))),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada")
    })
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
        }
    }

    // 🔹 FILTRAR POR DISCIPLINA
    @GetMapping("/disciplina/{disciplinaId}")
    @Operation(summary = "Listar turmas de uma disciplina específica")
    public ResponseEntity<List<TurmaDTO>> buscarPorDisciplina(@PathVariable Long disciplinaId) {
        return ResponseEntity.ok(service.buscarPorDisciplina(disciplinaId));
    }

    // 🔹 FILTRAR POR PROFESSOR
    @GetMapping("/professor/{professorId}")
    @Operation(summary = "Listar turmas de um professor específico")
    public ResponseEntity<List<TurmaDTO>> buscarPorProfessor(@PathVariable Long professorId) {
        return ResponseEntity.ok(service.buscarPorProfessor(professorId));
    }

    // 🔹 FILTRAR POR PERÍODO
    @GetMapping("/periodo/{periodo}")
    @Operation(summary = "Listar turmas por período (ex: 2025.1)")
    public ResponseEntity<List<TurmaDTO>> buscarPorPeriodo(@PathVariable String periodo) {
        return ResponseEntity.ok(service.buscarPorPeriodo(periodo));
    }

    // 🔹 CRIAR
    @PostMapping
    @Operation(summary = "Cadastrar nova turma")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Turma criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou duplicados")
    })
    public ResponseEntity<?> criar(@RequestBody TurmaDTO dto) {
        try {
            TurmaDTO criada = service.criar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(criada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // 🔹 ATUALIZAR
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados da turma (professor, capacidade, período)")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody TurmaDTO dto) {
        try {
            TurmaDTO atualizada = service.atualizar(id, dto);
            return ResponseEntity.ok(atualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
        }
    }

    // 🔹 EXCLUIR
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir turma")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Turma excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada")
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
