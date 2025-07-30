package com.mypetadmin.ps_empresa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypetadmin.ps_empresa.dto.EmpresaRequestDTO;
import com.mypetadmin.ps_empresa.dto.EmpresaResponseDTO;
import com.mypetadmin.ps_empresa.service.EmpresaService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmpresaController.class)
public class EmpresaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpresaService empresaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCriarEmpresaERetornarCreated() throws Exception {
        EmpresaRequestDTO requestDTO = new EmpresaRequestDTO();
        requestDTO.setDocumentNumber("17395568000151");
        requestDTO.setRazaoSocial("Empresa Teste");
        requestDTO.setNomeFantasia("Fantasia Teste");
        requestDTO.setTelefone("41999999999");
        requestDTO.setEmail("teste@empresa.com");
        requestDTO.setNomeTitular("Teste Titular");
        requestDTO.setRua("Rua A");
        requestDTO.setNumero("123");
        requestDTO.setComplemento("Bloco A");
        requestDTO.setBairro("Bairro Central");
        requestDTO.setCidade("Curitiba");
        requestDTO.setEstado("PR");
        requestDTO.setCep("01001000");

        EmpresaResponseDTO responseDTO = new EmpresaResponseDTO(
                UUID.randomUUID(), requestDTO.getDocumentNumber(), requestDTO.getRazaoSocial(), requestDTO.getNomeFantasia(), requestDTO.getTelefone(),
                requestDTO.getEmail(), requestDTO.getNomeTitular(), requestDTO.getCep(), requestDTO.getCidade(), requestDTO.getEstado(),
                "Rua A, 123, Bloco B - Bairro Central", "ATIVO"
        );

        when(empresaService.cadastrarEmpresa(requestDTO)).thenReturn(responseDTO);

        mockMvc.perform(post("/empresas/createEmpresas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.documentNumber").value("17395568000151"))
                .andExpect(jsonPath("$.razaoSocial").value("Empresa Teste"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

}
