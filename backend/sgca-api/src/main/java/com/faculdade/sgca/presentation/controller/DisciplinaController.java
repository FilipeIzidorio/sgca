package com.faculdade.sgca.presentation.controller;

import com.faculdade.sgca.application.dto.DisciplinaDTO;
import com.faculdade.sgca.application.service.DisciplinaService;
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
@RequestMapping("/api/v1/disciplinas")
@RequiredArgsConstructor
@Tag(name = "Disciplinas", description = "Gerenciamento de disciplinas (ADMIN e PROFESSOR)")
@SecurityRequirement(name = "bearerAuth")
public class DisciplinaController {

    private final DisciplinaService service;

    // 🔹 LISTAR TODAS
    @GetMapping
    @Operation(summary = "Listar todas as disciplinas")
    public ResponseEntity<List<DisciplinaDTO>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }

    // 🔹 BUSCAR POR ID
    @GetMapping("/{id}")
    @Operation(summary = "Buscar disciplina por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Disciplina encontrada",
                    content = @Content(schema = @Schema(implementation = DisciplinaDTO.class))),
            @ApiResponse(responseCode = "404", description = "Disciplina não encontrada")
    })
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // 🔹 BUSCAR POR CURSO
    @GetMapping("/curso/{cursoId}")
    @Operation(summary = "Listar disciplinas de um curso específico")
    public ResponseEntity<List<DisciplinaDTO>> buscarPorCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(service.buscarPorCurso(cursoId));
    }

    // 🔹 CRIAR
    @PostMapping
    @Operation(summary = "Cadastrar nova disciplina")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Disciplina criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou duplicados")
    })
    public ResponseEntity<?> criar(@RequestBody DisciplinaDTO dto) {
        try {
            DisciplinaDTO criada = service.criar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(criada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // 🔹 ATUALIZAR
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados da disciplina")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Disciplina atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Disciplina não encontrada")
    })
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody DisciplinaDTO dto) {
        try {
            DisciplinaDTO atualizada = service.atualizar(id, dto);
            return ResponseEntity.ok(atualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // 🔹 EXCLUIR
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir disciplina")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Disciplina excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Disciplina não encontrada")
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
