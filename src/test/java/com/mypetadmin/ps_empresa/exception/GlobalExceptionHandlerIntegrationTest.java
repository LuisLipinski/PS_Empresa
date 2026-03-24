package com.mypetadmin.ps_empresa.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypetadmin.ps_empresa.controller.EmpresaController;
import com.mypetadmin.ps_empresa.dto.EmpresaRequestDTO;
import com.mypetadmin.ps_empresa.service.EmpresaService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = EmpresaController.class)
class GlobalExceptionHandlerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpresaService empresaService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmpresaRequestDTO createValidEmpresaRequest() {
        EmpresaRequestDTO dto = new EmpresaRequestDTO();
        dto.setDocumentNumber("12345678000195");
        dto.setRazaoSocial("Empresa Teste Ltda");
        dto.setNomeFantasia("Empresa Teste");
        dto.setTelefone("11999999999");
        dto.setRua("Rua Teste");
        dto.setNumero("100");
        dto.setComplemento("Sala 1");
        dto.setBairro("Centro");
        dto.setCidade("São Paulo");
        dto.setEstado("SP");
        dto.setCep("01001000");
        return dto;
    }

    @Test
    void deveRetornar400QuandoEmpresaExistenteException() throws Exception {
        EmpresaRequestDTO dto = createValidEmpresaRequest();

        Mockito.when(empresaService.cadastrarEmpresa(any(EmpresaRequestDTO.class)))
                .thenThrow(new EmpresaExistenteException("CNPJ já cadastrado no sistema."));

        mockMvc.perform(post("/empresas/createEmpresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("CNPJ já cadastrado no sistema.")));
    }

    @Test
    void deveRetornar400QuandoIllegalArgumentException() throws Exception {
        EmpresaRequestDTO dto = createValidEmpresaRequest();

        Mockito.when(empresaService.cadastrarEmpresa(any(EmpresaRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("CNPJ inválido."));

        mockMvc.perform(post("/empresas/createEmpresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("CNPJ inválido.")));
    }

    @Test
    void deveRetornar500QuandoRuntimeExceptionGenerica() throws Exception {
        EmpresaRequestDTO dto = createValidEmpresaRequest();

        Mockito.when(empresaService.cadastrarEmpresa(any(EmpresaRequestDTO.class)))
                .thenThrow(new RuntimeException("Erro inesperado"));

        mockMvc.perform(post("/empresas/createEmpresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Erro interno no servidor. Tente novamente mais tarde.")));
    }

    @Test
    void deveRetornar400QuandoCnpjInvalidException() throws Exception {
        EmpresaRequestDTO dto = createValidEmpresaRequest();

        Mockito.when(empresaService.cadastrarEmpresa(any(EmpresaRequestDTO.class)))
                .thenThrow(new CnpjInvalidException("CNPJ inválido."));

        mockMvc.perform(post("/empresas/createEmpresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("CNPJ inválido."));
    }

    @Test
    void deveRetornar400QuandoCorpoDaRequisicaoAusenteOuInvalido() throws Exception {
        mockMvc.perform(post("/empresas/createEmpresas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Corpo da requisição ausente ou inválido."));
    }
}