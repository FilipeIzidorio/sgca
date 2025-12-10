package com.faculdade.sgca.presentation.controller;

import com.faculdade.sgca.application.service.PresencaService;
import com.faculdade.sgca.infrastructure.security.JwtAuthFilter;
import stub.PresencaStub;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = PresencaController.class,
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
class PresencaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PresencaService service;

    @Test
    void listarTodas() throws Exception {
        when(service.listarTodas())
                .thenReturn(List.of(PresencaStub.dto()));

        mockMvc.perform(get("/api/v1/presencas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].presente").value(true));
    }

    @Test
    void buscarPorId() throws Exception {
        when(service.buscarPorId(1L))
                .thenReturn(PresencaStub.dto());

        mockMvc.perform(get("/api/v1/presencas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matriculaId").value(10));
    }

    @Test
    void buscarPorTurma() throws Exception {
        when(service.buscarPorTurma(20L))
                .thenReturn(List.of(PresencaStub.dto()));

        mockMvc.perform(get("/api/v1/presencas/turma/20"))
                .andExpect(status().isOk());
    }

    @Test
    void buscarPorMatricula() throws Exception {
        when(service.buscarPorMatricula(10L))
                .thenReturn(List.of(PresencaStub.dto()));

        mockMvc.perform(get("/api/v1/presencas/matricula/10"))
                .andExpect(status().isOk());
    }

    @Test
    void criar() throws Exception {
        when(service.criar(any()))
                .thenReturn(PresencaStub.dto());

        mockMvc.perform(post("/api/v1/presencas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "matriculaId": 10,
                                  "turmaId": 20,
                                  "presente": true
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void atualizar() throws Exception {
        when(service.atualizar(1L, PresencaStub.dto().isPresente()))
                .thenReturn(PresencaStub.dto());

        mockMvc.perform(put("/api/v1/presencas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "presente": false
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void excluir() throws Exception {
        mockMvc.perform(delete("/api/v1/presencas/1"))
                .andExpect(status().isNoContent());
    }
}
