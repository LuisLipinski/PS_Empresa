package com.mypetadmin.ps_empresa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypetadmin.ps_empresa.dto.EmpresaRequestDTO;
import com.mypetadmin.ps_empresa.dto.EmpresaResponseDTO;
import com.mypetadmin.ps_empresa.enums.StatusEmpresa;
import com.mypetadmin.ps_empresa.exception.EmpresaNaoEncontradaException;
import com.mypetadmin.ps_empresa.service.EmpresaService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
                "Rua A, 123, Bloco B - Bairro Central", StatusEmpresa.AGUARDANDO_PAGAMENTO
        );

        when(empresaService.cadastrarEmpresa(requestDTO)).thenReturn(responseDTO);

        mockMvc.perform(post("/empresas/createEmpresas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.documentNumber").value("17395568000151"))
                .andExpect(jsonPath("$.razaoSocial").value("Empresa Teste"))
                .andExpect(jsonPath("$.status").value(StatusEmpresa.AGUARDANDO_PAGAMENTO.name()));
    }

    @Test
    void atualizaStatus_QuandoDadosValidos_entaoRetorna200() throws Exception {
        UUID empresaID = UUID.randomUUID();
        StatusEmpresa novoStatus = StatusEmpresa.ATIVO;
        Mockito.doNothing().when(empresaService).atualizarStatus(eq(empresaID), eq(novoStatus));

        mockMvc.perform(put("/empresas/atualizarStatus")
                .param("empresaId", empresaID.toString())
                .param("novoStatus", novoStatus.name())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("Status atualizado com sucesso"))
            .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    void atualizarStatus_quandoEmpresaNaoEncontrada_retorna404() throws Exception {
        UUID empresaId = UUID.randomUUID();
        StatusEmpresa novoStatus = StatusEmpresa.INATIVO;
        Mockito.doThrow(new EmpresaNaoEncontradaException("Empresa não encontrada"))
                .when(empresaService).atualizarStatus(eq(empresaId), eq(novoStatus));

        mockMvc.perform(put("/empresas/atualizarStatus")
                .param("empresaId", empresaId.toString())
                .param("novoStatus", novoStatus.name())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Empresa não encontrada"));
    }

    @Test
    void atualizarStatus_quandoParametrosAusentes_entaoRetorna400() throws Exception{
        mockMvc.perform(put("/empresas/atualizarStatus")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

}
