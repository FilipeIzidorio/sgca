package com.faculdade.sgca.presentation.controller;

import com.faculdade.sgca.application.service.MatriculaService;
import com.faculdade.sgca.infrastructure.security.JwtAuthFilter;
import stub.MatriculaStub;

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
        controllers = MatriculaController.class,
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
class MatriculaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MatriculaService service;

    @Test
    void listarTodas() throws Exception {
        when(service.listarTodas())
                .thenReturn(List.of(MatriculaStub.dto()));

        mockMvc.perform(get("/api/v1/matriculas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].situacao").value("ATIVA"));
    }

    @Test
    void buscarPorId() throws Exception {
        when(service.buscarPorId(1L))
                .thenReturn(MatriculaStub.dto());

        mockMvc.perform(get("/api/v1/matriculas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alunoId").value(10));
    }

    @Test
    void buscarPorAluno() throws Exception {
        when(service.buscarPorAluno(10L))
                .thenReturn(List.of(MatriculaStub.dto()));

        mockMvc.perform(get("/api/v1/matriculas/aluno/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].turmaId").value(20));
    }

    @Test
    void buscarPorTurma() throws Exception {
        when(service.buscarPorTurma(20L))
                .thenReturn(List.of(MatriculaStub.dto()));

        mockMvc.perform(get("/api/v1/matriculas/turma/20"))
                .andExpect(status().isOk());
    }

    @Test
    void criar() throws Exception {
        when(service.criar(any()))
                .thenReturn(MatriculaStub.dto());

        mockMvc.perform(post("/api/v1/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "alunoId": 10,
                                  "turmaId": 20,
                                  "situacao": "ATIVA"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void atualizarSituacao() throws Exception {
        when(service.atualizarSituacao(1L, "TRANCADA"))
                .thenReturn(MatriculaStub.dto());

        mockMvc.perform(patch("/api/v1/matriculas/1/situacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "situacao": "TRANCADA"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void excluir() throws Exception {
        mockMvc.perform(delete("/api/v1/matriculas/1"))
                .andExpect(status().isNoContent());
    }
}
