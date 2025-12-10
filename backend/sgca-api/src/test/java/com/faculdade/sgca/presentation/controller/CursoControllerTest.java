package com.faculdade.sgca.presentation.controller;

import com.faculdade.sgca.application.service.CursoService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import stub.CursoStub;
import com.faculdade.sgca.infrastructure.security.JwtAuthFilter;

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
        controllers = CursoController.class,
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
class CursoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CursoService service;

    @Test
    void listarTodos() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(CursoStub.dto()));

        mockMvc.perform(get("/api/v1/cursos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("ADS"));
    }

    @Test
    void buscarPorId() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(CursoStub.dto());

        mockMvc.perform(get("/api/v1/cursos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Análise e Desenvolvimento de Sistemas"));
    }

    @Test
    void criar() throws Exception {
        when(service.criar(any())).thenReturn(CursoStub.dto());

        mockMvc.perform(post("/api/v1/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "ADS",
                                  "nome": "Análise e Desenvolvimento de Sistemas",
                                  "cargaHoraria": 2400,
                                  "descricao": "Curso de tecnologia"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void atualizar() throws Exception {
        when(service.atualizar(1L, CursoStub.dto()))
                .thenReturn(CursoStub.dto());

        mockMvc.perform(put("/api/v1/cursos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "ADS",
                                  "nome": "ADS Atualizado",
                                  "cargaHoraria": 2600,
                                  "descricao": "Curso atualizado"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void excluir() throws Exception {
        mockMvc.perform(delete("/api/v1/cursos/1"))
                .andExpect(status().isNoContent());
    }
}
