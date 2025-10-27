package com.faculdade.sgca.presentation.controller;

import com.faculdade.sgca.domain.model.Usuario;
import com.faculdade.sgca.infrastructure.repository.UsuarioRepository;
import com.faculdade.sgca.infrastructure.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Cadastro e login com JWT no SGCA")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // ============================================
    // CADASTRAR NOVO USUÁRIO
    // ============================================
    @PostMapping("/signup")
    @Operation(
            summary = "Cadastrar novo usuário",
            description = "Cria um novo usuário no sistema e retorna o token JWT."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Senhas não coincidem ou dados inválidos"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    })
    @RequestBody(
            required = true,
            description = "Dados para criar um novo usuário",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                        {
                          "nome": "João da Silva",
                          "email": "joao.silva@example.com",
                          "senha": "123456",
                          "confirmarSenha": "123456",
                          "perfil": "ADMIN"
                        }
                    """)
            )
    )
    public ResponseEntity<?> signup(@Valid @org.springframework.web.bind.annotation.RequestBody Usuario usuario) {

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("erro", "E-mail já cadastrado."));
        }

        if (!usuario.getSenha().equals(usuario.getConfirmarSenha())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", "As senhas não coincidem."));
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuarioRepository.save(usuario);

        String token = jwtService.generateToken(usuario.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "mensagem", "Usuário cadastrado com sucesso!",
                "usuario", Map.of(
                        "id", usuario.getId(),
                        "nome", usuario.getNome(),
                        "email", usuario.getEmail(),
                        "perfil", usuario.getPerfil()
                ),
                "token", token
        ));
    }

    // ============================================
    // LOGIN (SEM USERDETAILSSERVICE)
    // ============================================
    @PostMapping("/login")
    @Operation(
            summary = "Login de usuário",
            description = "Autentica o usuário com e-mail e senha e retorna um token JWT."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    @RequestBody(
            required = true,
            description = "Credenciais de login do usuário",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                        {
                          "email": "joao.silva@example.com",
                          "senha": "123456"
                        }
                    """)
            )
    )
    public ResponseEntity<?> login(@org.springframework.web.bind.annotation.RequestBody Map<String, String> credenciais) {
        String email = credenciais.get("email");
        String senha = credenciais.get("senha");

        if (email == null || senha == null) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Campos 'email' e 'senha' são obrigatórios."));
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElse(null);

        if (usuario == null || !passwordEncoder.matches(senha, usuario.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "E-mail ou senha inválidos."));
        }

        String token = jwtService.generateToken(email);

        return ResponseEntity.ok(Map.of(
                "mensagem", "Login realizado com sucesso!",
                "usuario", Map.of(
                        "id", usuario.getId(),
                        "nome", usuario.getNome(),
                        "email", usuario.getEmail(),
                        "perfil", usuario.getPerfil()
                ),
                "token", token
        ));
    }

    // ============================================
    // USUÁRIO AUTENTICADO (SEM USERDETAILS)
    // ============================================
    @GetMapping("/me")
    @Operation(
            summary = "Ver dados do usuário autenticado (requer JWT)",
            description = "Retorna as informações do usuário logado com base no token JWT."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário autenticado retornado"),
            @ApiResponse(responseCode = "401", description = "Token inválido ou ausente")
    })
    public ResponseEntity<?> me(@RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Token JWT ausente ou inválido."));
        }

        String jwt = authHeader.substring(7);
        String email = jwtService.extractUsername(jwt);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        return ResponseEntity.ok(Map.of(
                "id", usuario.getId(),
                "nome", usuario.getNome(),
                "email", usuario.getEmail(),
                "perfil", usuario.getPerfil()
        ));
    }
}
