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
                        // Origem do seu frontend Vite
                        .allowedOriginPatterns("http://localhost:5173")

                        // Headers que o front pode enviar
                        .allowedHeaders("*")

                        // Métodos permitidos
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")

                        // Permitir enviar Authorization: Bearer <token>
                        .allowCredentials(true)

                        // Cache do preflight em segundos
                        .maxAge(3600);
            }
        };
    }
}
