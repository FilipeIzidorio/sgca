package com.faculdade.sgca.presentation.controller;

import com.faculdade.sgca.application.dto.AlunoDTO;
import com.faculdade.sgca.application.service.AlunoService;
import com.faculdade.sgca.infrastructure.security.JwtAuthFilter;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import stub.AlunoStub;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AlunoController.class,
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
class AlunoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlunoService service;

    @Test
    void deveCriarAlunoViaController() throws Exception {
        AlunoDTO dto = AlunoStub.dtoValido();

        when(service.criar(any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Maria Silva",
                                  "email": "maria@email.com",
                                  "cpf": "12345678900",
                                  "status": "ATIVO"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Maria Silva"));
    }

    @Test
    void deveBuscarAlunoPorId() throws Exception {
        when(service.buscarPorId(1L))
                .thenReturn(AlunoStub.dtoValido());

        mockMvc.perform(get("/api/v1/alunos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("maria@email.com"));
    }

    @Test
    void deveExcluirAluno() throws Exception {
        mockMvc.perform(delete("/api/v1/alunos/1"))
                .andExpect(status().isNoContent());
    }
}
