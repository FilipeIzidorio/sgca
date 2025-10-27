package com.faculdade.sgca.presentation.controller;

import com.faculdade.sgca.application.dto.AvaliacaoDTO;
import com.faculdade.sgca.application.service.AvaliacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/avaliacoes")
@RequiredArgsConstructor
@Tag(name = "Avaliações", description = "Gerenciamento de avaliações por turma")
@SecurityRequirement(name = "bearerAuth")
public class AvaliacaoController {

    private final AvaliacaoService service;

    // =============================
    // LISTAR
    // =============================
    @GetMapping
    @Operation(summary = "Listar todas as avaliações")
    public ResponseEntity<List<AvaliacaoDTO>> listar() {
        List<AvaliacaoDTO> avaliacoes = service.listar();
        if (avaliacoes.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(avaliacoes);
    }

    // =============================
    // BUSCAR POR ID
    // =============================
    @GetMapping("/{id}")
    @Operation(summary = "Buscar avaliação por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Avaliação encontrada"),
            @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
        }
    }

    // =============================
    // CRIAR
    // =============================
    @PostMapping
    @Operation(
            summary = "Criar nova avaliação",
            description = "Cria uma avaliação associada a uma turma. A soma dos pesos da turma não pode ultrapassar 100%.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AvaliacaoDTO.class),
                            examples = @ExampleObject(value = """
                                {
                                  "titulo": "Prova Final",
                                  "peso": 40,
                                  "tipo": "PROVA",
                                  "turmaId": 5
                                }
                            """)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Avaliação criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Campos inválidos ou soma de pesos > 100%")
    })
    public ResponseEntity<?> criar(@Valid @RequestBody AvaliacaoDTO dto) {
        try {
            AvaliacaoDTO criado = service.criar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(criado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // =============================
    // ATUALIZAR
    // =============================
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados de uma avaliação")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody AvaliacaoDTO dto) {
        try {
            AvaliacaoDTO atualizado = service.atualizar(id, dto);
            return ResponseEntity.ok(atualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // =============================
    // EXCLUIR
    // =============================
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir uma avaliação existente")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        try {
            service.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
        }
    }
}
