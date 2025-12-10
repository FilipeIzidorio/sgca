package com.faculdade.sgca.presentation.controller;

import com.faculdade.sgca.application.service.TurmaService;
import com.faculdade.sgca.infrastructure.security.JwtAuthFilter;
import stub.TurmaStub;

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
        controllers = TurmaController.class,
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
class TurmaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TurmaService service;

    @Test
    void listarTodas() throws Exception {
        when(service.listarTodas())
                .thenReturn(List.of(TurmaStub.dto()));

        mockMvc.perform(get("/api/v1/turmas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].periodo").value("2025.1"));
    }

    @Test
    void buscarPorId() throws Exception {
        when(service.buscarPorId(1L))
                .thenReturn(TurmaStub.dto());

        mockMvc.perform(get("/api/v1/turmas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disciplinaId").value(10));
    }

    @Test
    void buscarPorDisciplina() throws Exception {
        when(service.buscarPorDisciplina(10L))
                .thenReturn(List.of(TurmaStub.dto()));

        mockMvc.perform(get("/api/v1/turmas/disciplina/10"))
                .andExpect(status().isOk());
    }

    @Test
    void buscarPorProfessor() throws Exception {
        when(service.buscarPorProfessor(20L))
                .thenReturn(List.of(TurmaStub.dto()));

        mockMvc.perform(get("/api/v1/turmas/professor/20"))
                .andExpect(status().isOk());
    }

    @Test
    void buscarPorPeriodo() throws Exception {
        when(service.buscarPorPeriodo("2025.1"))
                .thenReturn(List.of(TurmaStub.dto()));

        mockMvc.perform(get("/api/v1/turmas/periodo/2025.1"))
                .andExpect(status().isOk());
    }

    @Test
    void criar() throws Exception {
        when(service.criar(any()))
                .thenReturn(TurmaStub.dto());

        mockMvc.perform(post("/api/v1/turmas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "disciplinaId": 10,
                                  "professorId": 20,
                                  "periodo": "2025.1"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void atualizar() throws Exception {
        when(service.atualizar(1L, TurmaStub.dto()))
                .thenReturn(TurmaStub.dto());

        mockMvc.perform(put("/api/v1/turmas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "periodo": "2025.2"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void excluir() throws Exception {
        mockMvc.perform(delete("/api/v1/turmas/1"))
                .andExpect(status().isNoContent());
    }
}
