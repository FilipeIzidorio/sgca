package com.faculdade.sgca.presentation.controller;

import com.faculdade.sgca.application.dto.CursoDTO;
import com.faculdade.sgca.application.service.CursoService;
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
@RequestMapping("/api/v1/cursos")
@RequiredArgsConstructor
@Tag(name = "Cursos", description = "Gerenciamento de cursos (somente ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class CursoController {

    private final CursoService service;

    // 🔹 LISTAR
    @GetMapping
    @Operation(summary = "Listar todos os cursos")
    public ResponseEntity<List<CursoDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // 🔹 BUSCAR POR ID
    @GetMapping("/{id}")
    @Operation(summary = "Buscar curso por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Curso encontrado",
                    content = @Content(schema = @Schema(implementation = CursoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Curso não encontrado")
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
    @Operation(summary = "Cadastrar novo curso")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Curso criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou código duplicado")
    })
    public ResponseEntity<?> criar(@RequestBody CursoDTO dto) {
        try {
            CursoDTO criado = service.criar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(criado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // 🔹 ATUALIZAR
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar curso existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Curso atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Curso não encontrado")
    })
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody CursoDTO dto) {
        try {
            CursoDTO atualizado = service.atualizar(id, dto);
            return ResponseEntity.ok(atualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // 🔹 EXCLUIR
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir curso")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Curso excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Curso não encontrado")
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
