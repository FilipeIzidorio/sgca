package com.faculdade.sgca.presentation.controller;

import com.faculdade.sgca.application.dto.UsuarioDTO;
import com.faculdade.sgca.application.service.UsuarioService;
import com.faculdade.sgca.domain.model.PerfilUsuario;
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
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gerenciamento de usuários do sistema (somente administradores)")
@SecurityRequirement(name = "bearerAuth") // 🔒 exige token JWT no Swagger
public class UsuarioController {

    private final UsuarioService service;

    // ========================
    // LISTAR TODOS
    // ========================
    @GetMapping
    @Operation(summary = "Listar todos os usuários (somente ADMIN)")
    public ResponseEntity<List<UsuarioDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // ========================
    // BUSCAR POR ID
    // ========================
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado",
                    content = @Content(schema = @Schema(implementation = UsuarioDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // ========================
    // ATUALIZAR
    // ========================
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados do usuário (ADMIN)")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String nome = body.get("nome");
        String email = body.get("email");
        String senha = body.get("senha");
        String confirmarSenha = body.get("confirmarSenha");
        String perfilStr = body.get("perfil");

        if (senha != null && confirmarSenha != null && !senha.equals(confirmarSenha)) {
            return ResponseEntity.badRequest().body(Map.of("erro", "As senhas não coincidem."));
        }

        PerfilUsuario perfil = null;
        if (perfilStr != null && !perfilStr.isBlank()) {
            try {
                perfil = PerfilUsuario.valueOf(perfilStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("erro", "Perfil inválido. Use ADMIN ou PROF."));
            }
        }

        try {
            UsuarioDTO atualizado = service.atualizar(id, nome, email, senha, perfil);
            return ResponseEntity.ok(atualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // ========================
    // EXCLUIR
    // ========================
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir usuário (somente ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
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
