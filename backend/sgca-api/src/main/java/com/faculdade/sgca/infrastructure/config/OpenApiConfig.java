package com.faculdade.sgca.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "SGCA - Sistema de Gerenciamento de Cursos e Alunos",
                version = "1.0",
                description = "API para gerenciamento de usuários, cursos, turmas, avaliações e notas com autenticação JWT."
        ),
        servers = {
                @Server(url = "http://localhost:8081", description = "Servidor local de desenvolvimento")
        },
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Cole aqui o token JWT obtido em `/api/v1/auth/login` ou `/api/v1/auth/signup`"
)
public class OpenApiConfig {
}
