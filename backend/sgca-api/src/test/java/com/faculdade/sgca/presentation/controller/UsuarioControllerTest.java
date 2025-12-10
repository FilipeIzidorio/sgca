package com.faculdade.sgca.presentation.controller;

import com.faculdade.sgca.application.service.UsuarioService;
import com.faculdade.sgca.infrastructure.security.JwtAuthFilter;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import stub.UsuarioStub;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(
        controllers = UsuarioController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        },
        excludeFilters = {
                @ComponentScan.Filter(
                        type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE,
                        classes = JwtAuthFilter.class
                )
        }
)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService service;

    @Test
    void listarTodos() throws Exception {
        when(service.listarTodos())
                .thenReturn(List.of(UsuarioStub.dto()));

        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].perfil").value("ADMIN"));
    }

    @Test
    void buscarPorId() throws Exception {
        when(service.buscarPorId(1L))
                .thenReturn(UsuarioStub.dto());

        mockMvc.perform(get("/api/v1/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@email.com"));
    }

    @Test
    void criar() throws Exception {
        when(service.atualizar(any(), any(), any(), any(), any()))
                .thenReturn(UsuarioStub.dto());

        mockMvc.perform(put("/api/v1/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "nome": "Admin",
                          "email": "admin@email.com",
                          "senha": "123456",
                          "confirmarSenha": "123456",
                          "perfil": "ADMIN"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Admin"));
    }



    @Test
    void excluir() throws Exception {
        mockMvc.perform(delete("/api/v1/usuarios/1"))
                .andExpect(status().isNoContent());
    }
}
