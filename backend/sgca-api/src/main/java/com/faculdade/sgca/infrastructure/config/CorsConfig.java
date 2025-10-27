package com.faculdade.sgca.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        // 🌐 Libera origens — use "*" apenas em ambiente local
                        // Se quiser especificar:
                        // .allowedOriginPatterns("http://localhost:8080", "http://localhost:8081")
                        .allowedOriginPatterns("*")

                        // 🔑 Libera todos os headers (Authorization, Content-Type, etc.)
                        .allowedHeaders("*")

                        // ⚙️ Libera métodos HTTP comuns
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")

                        // 🧠 Permite envio de cookies, tokens JWT e autenticação
                        .allowCredentials(true)

                        // ⏱️ Tempo (em segundos) que o navegador pode cachear a política CORS
                        .maxAge(3600);
            }
        };
    }
}
