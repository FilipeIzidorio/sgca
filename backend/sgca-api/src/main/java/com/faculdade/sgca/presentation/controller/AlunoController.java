package com.faculdade.sgca.presentation.controller;

import com.faculdade.sgca.application.dto.AlunoDTO;
import com.faculdade.sgca.application.service.AlunoService;
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
@RequestMapping("/api/v1/alunos")
@RequiredArgsConstructor
@Tag(name = "Alunos", description = "Gerenciamento de alunos (somente ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class AlunoController {

    private final AlunoService service;

    // 🔹 LISTAR
    @GetMapping
    @Operation(summary = "Listar todos os alunos")
    public ResponseEntity<List<AlunoDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // 🔹 BUSCAR POR ID
    @GetMapping("/{id}")
    @Operation(summary = "Buscar aluno por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Aluno encontrado",
                    content = @Content(schema = @Schema(implementation = AlunoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado")
    })
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // 🔹 CRIAR
    @PostMapping
    @Operation(summary = "Cadastrar novo aluno")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Aluno criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou duplicados")
    })
    public ResponseEntity<?> criar(@RequestBody AlunoDTO dto) {
        try {
            AlunoDTO criado = service.criar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(criado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // 🔹 ATUALIZAR
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados do aluno")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Aluno atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado")
    })
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody AlunoDTO dto) {
        try {
            AlunoDTO atualizado = service.atualizar(id, dto);
            return ResponseEntity.ok(atualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // 🔹 EXCLUIR
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir aluno")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Aluno excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado")
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
