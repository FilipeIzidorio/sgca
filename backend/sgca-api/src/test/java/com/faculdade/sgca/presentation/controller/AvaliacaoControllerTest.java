package com.faculdade.sgca.presentation.controller;


import com.faculdade.sgca.application.service.AvaliacaoService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import stub.AvaliacaoStub;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AvaliacaoController.class,
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
class AvaliacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AvaliacaoService service;

    @Test
    void listar() throws Exception {
        when(service.listar()).thenReturn(List.of(AvaliacaoStub.dto()));

        mockMvc.perform(get("/api/v1/avaliacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Prova Final"));
    }

    @Test
    void buscarPorId() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(AvaliacaoStub.dto());

        mockMvc.perform(get("/api/v1/avaliacoes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("PROVA"));
    }

    @Test
    void criar() throws Exception {
        when(service.criar(AvaliacaoStub.dto())).thenReturn(AvaliacaoStub.dto());

        mockMvc.perform(post("/api/v1/avaliacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo": "Prova Final",
                                  "peso": 40,
                                  "tipo": "PROVA",
                                  "turmaId": 10
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void atualizar() throws Exception {
        when(service.atualizar(1L, AvaliacaoStub.dto()))
                .thenReturn(AvaliacaoStub.dto());

        mockMvc.perform(put("/api/v1/avaliacoes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo": "Prova Atualizada",
                                  "peso": 50,
                                  "tipo": "PROVA",
                                  "turmaId": 10
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void excluir() throws Exception {
        mockMvc.perform(delete("/api/v1/avaliacoes/1"))
                .andExpect(status().isNoContent());
    }
}
