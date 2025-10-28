package com.faculdade.sgca.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // habilita CORS usando a config que você definiu em CorsConfig
                .cors(cors -> { })
                // desabilita CSRF porque sua API é stateless com JWT (sem sessão web tradicional)
                .csrf(csrf -> csrf.disable())
                // não cria nem mantém sessão no server
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // -------------------------------------------------
                        // 1) libera o preflight OPTIONS para QUALQUER rota
                        //    Isso evita que o navegador tome 401/403 no preflight
                        //    e resolva o erro de CORS "It does not have HTTP ok status"
                        // -------------------------------------------------
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // -------------------------------------------------
                        // 2) libera rotas públicas (login/signup, swagger, erro)
                        // -------------------------------------------------
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/error"
                        ).permitAll()

                        // -------------------------------------------------
                        // 3) qualquer outra rota exige estar autenticado com JWT
                        // -------------------------------------------------
                        .anyRequest().authenticated()
                )
                // registra o filtro que valida o JWT ANTES do filtro padrão de usuário/senha
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // encoder padrão
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager que o Spring usa internamente pra autenticação
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
