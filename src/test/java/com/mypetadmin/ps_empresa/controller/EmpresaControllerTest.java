package com.mypetadmin.ps_empresa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypetadmin.ps_empresa.dto.EmpresaRequestDTO;
import com.mypetadmin.ps_empresa.dto.EmpresaResponseDTO;
import com.mypetadmin.ps_empresa.enums.StatusEmpresa;
import com.mypetadmin.ps_empresa.exception.EmpresaNaoEncontradaException;
import com.mypetadmin.ps_empresa.service.EmpresaService;
import com.mypetadmin.ps_empresa.util.CnpjValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Test
    void buscarEmpresas_comFiltros_retornaLista() throws Exception {
        EmpresaResponseDTO empresa1 = new EmpresaResponseDTO();
        empresa1.setId(UUID.randomUUID());
        empresa1.setRazaoSocial("Pet Shop ABC");
        empresa1.setDocumentNumber("12345678000199");
        empresa1.setStatus(StatusEmpresa.ATIVO);

        EmpresaResponseDTO empresa2 = new EmpresaResponseDTO();
        empresa2.setId(UUID.randomUUID());
        empresa2.setRazaoSocial("Pet Shop XYZ");
        empresa2.setDocumentNumber("98765432000188");
        empresa2.setStatus(StatusEmpresa.AGUARDANDO_PAGAMENTO);

        List<EmpresaResponseDTO> empresas = Arrays.asList(empresa1, empresa2);

        when(empresaService.getAllEmpresaSorted(
                any(), any(), any(), any(), any(), any()
        )).thenReturn(empresas);

        mockMvc.perform(get("/empresas/buscaEmpresas")
                        .param("status", StatusEmpresa.AGUARDANDO_PAGAMENTO.name())
                        .param("razaoSocial", "Pet Shop")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].razaoSocial").value("Pet Shop ABC"))
                .andExpect(jsonPath("$[1].razaoSocial").value("Pet Shop XYZ"));
    }

    @Test
    void buscarEmpresas_semResultados_retornaListaVazia() throws Exception {
        when(empresaService.getAllEmpresaSorted(any(), any(), any(), any(), any(), any()))
                .thenReturn(Arrays.asList());

        mockMvc.perform(get("/empresas/buscaEmpresas")
                        .param("documentNumber", "00000000000000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getEmpresaById_sucesso() throws Exception {
        UUID empresaId = UUID.randomUUID();
        EmpresaResponseDTO responseDTO = new EmpresaResponseDTO();
        responseDTO.setId(empresaId);
        responseDTO.setRazaoSocial("Pet Shop Teste");
        responseDTO.setDocumentNumber("12345678000199");


        when(empresaService.getEmpresaById(empresaId)).thenReturn(responseDTO);

        mockMvc.perform(get("/empresas/buscaEmpresas/{id}", empresaId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(empresaId.toString()))
                .andExpect(jsonPath("$.razaoSocial").value("Pet Shop Teste"))
                .andExpect(jsonPath("$.documentNumber").value("12345678000199"));
    }

    @Test
    void getEmpresaById_naoEncontrada_retorna404() throws Exception {
        UUID empresaId = UUID.randomUUID();
        when(empresaService.getEmpresaById(empresaId))
                .thenThrow(new EmpresaNaoEncontradaException("Empresa não encontrada"));

        mockMvc.perform(get("/empresas/buscaEmpresas/{id}", empresaId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Empresa não encontrada"));
    }

    @Test
    void deleteEmpresaById_sucesso() throws Exception {
        UUID empresaId = UUID.randomUUID();

        Mockito.doNothing().when(empresaService).deleteEmpresaById(empresaId);

        mockMvc.perform(delete("/empresas/excluirEmpresa/{id}", empresaId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // 204
    }

    @Test
    void deleteEmpresaById_naoEncontrada_retorna404() throws Exception {
        UUID empresaId = UUID.randomUUID();

        Mockito.doThrow(new EmpresaNaoEncontradaException("Empresa não encontrada com o id: " + empresaId))
                .when(empresaService).deleteEmpresaById(empresaId);

        mockMvc.perform(delete("/empresas/excluirEmpresa/{id}", empresaId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Empresa não encontrada com o id: " + empresaId));
    }


}
