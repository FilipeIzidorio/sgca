package com.faculdade.sgca.presentation.controller;

import com.faculdade.sgca.application.service.DisciplinaService;
import com.faculdade.sgca.infrastructure.security.JwtAuthFilter;
import stub.DisciplinaStub;

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
        controllers = DisciplinaController.class,
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
class DisciplinaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DisciplinaService service;

    @Test
    void listarTodas() throws Exception {
        when(service.listarTodas()).thenReturn(List.of(DisciplinaStub.dto()));

        mockMvc.perform(get("/api/v1/disciplinas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("POO"));
    }

    @Test
    void buscarPorId() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(DisciplinaStub.dto());

        mockMvc.perform(get("/api/v1/disciplinas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome")
                        .value("Programação Orientada a Objetos"));
    }

    @Test
    void buscarPorCurso() throws Exception {
        when(service.buscarPorCurso(10L))
                .thenReturn(List.of(DisciplinaStub.dto()));

        mockMvc.perform(get("/api/v1/disciplinas/curso/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("POO"));
    }

    @Test
    void criar() throws Exception {
        when(service.criar(any())).thenReturn(DisciplinaStub.dto());

        mockMvc.perform(post("/api/v1/disciplinas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "POO",
                                  "nome": "Programação Orientada a Objetos",
                                  "cargaHoraria": 80,
                                  "cursoId": 10
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void atualizar() throws Exception {
        when(service.atualizar(1L, DisciplinaStub.dto()))
                .thenReturn(DisciplinaStub.dto());

        mockMvc.perform(put("/api/v1/disciplinas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "POO",
                                  "nome": "POO Atualizada",
                                  "cargaHoraria": 90,
                                  "cursoId": 10
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void excluir() throws Exception {
        mockMvc.perform(delete("/api/v1/disciplinas/1"))
                .andExpect(status().isNoContent());
    }
}
