package com.faculdade.sgca.presentation.controller;

import com.faculdade.sgca.application.service.NotaService;
import com.faculdade.sgca.infrastructure.security.JwtAuthFilter;
import stub.NotaStub;

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
        controllers = NotaController.class,
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
class NotaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotaService service;

    @Test
    void listarTodas() throws Exception {
        when(service.listarTodas())
                .thenReturn(List.of(NotaStub.dto()));

        mockMvc.perform(get("/api/v1/notas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].valor").value(8.5));
    }

    @Test
    void buscarPorId() throws Exception {
        when(service.buscarPorId(1L))
                .thenReturn(NotaStub.dto());

        mockMvc.perform(get("/api/v1/notas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matriculaId").value(10));
    }

    @Test
    void buscarPorMatricula() throws Exception {
        when(service.buscarPorMatricula(10L))
                .thenReturn(List.of(NotaStub.dto()));

        mockMvc.perform(get("/api/v1/notas/matricula/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].valor").value(8.5));
    }

    @Test
    void buscarPorTurma() throws Exception {
        when(service.buscarPorTurma(20L))
                .thenReturn(List.of(NotaStub.dto()));

        mockMvc.perform(get("/api/v1/notas/turma/20"))
                .andExpect(status().isOk());
    }

    @Test
    void criar() throws Exception {
        when(service.criar(any()))
                .thenReturn(NotaStub.dto());

        mockMvc.perform(post("/api/v1/notas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "matriculaId": 10,
                                  "turmaId": 20,
                                  "valor": 8.5
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void atualizarValor() throws Exception {
        when(service.atualizarValor(1L, 9.0))
                .thenReturn(NotaStub.dto());

        mockMvc.perform(patch("/api/v1/notas/1/valor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "valor": 9.0
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void excluir() throws Exception {
        mockMvc.perform(delete("/api/v1/notas/1"))
                .andExpect(status().isNoContent());
    }
}
