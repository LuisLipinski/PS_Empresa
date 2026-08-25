package com.mypetadmin.ps_empresa.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypetadmin.ps_empresa.controller.InternalEmpresaController;
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

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = InternalEmpresaController.class)
class GlobalExceptionHandlerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpresaService empresaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void empresaExistenteRetorna409() throws Exception {
        EmpresaRequestDTO dto = createValidEmpresaRequest();
        Mockito.when(empresaService.cadastrarEmpresa(any(EmpresaRequestDTO.class)))
                .thenThrow(new EmpresaExistenteException("CNPJ já cadastrado no sistema."));

        mockMvc.perform(post("/internal/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EMPRESA_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("CNPJ já cadastrado no sistema."));
    }

    @Test
    void emailExistenteRetorna409() throws Exception {
        EmpresaRequestDTO dto = createValidEmpresaRequest();
        Mockito.when(empresaService.cadastrarEmpresa(any(EmpresaRequestDTO.class)))
                .thenThrow(new EmailExistenteException("Email já cadastrado no sistema, informe outro email."));

        mockMvc.perform(post("/internal/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_EXISTS"));
    }

    @Test
    void cnpjInvalidoRetorna400() throws Exception {
        EmpresaRequestDTO dto = createValidEmpresaRequest();
        Mockito.when(empresaService.cadastrarEmpresa(any(EmpresaRequestDTO.class)))
                .thenThrow(new CnpjInvalidException("CNPJ inválido."));

        mockMvc.perform(post("/internal/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_CNPJ"))
                .andExpect(jsonPath("$.message").value("CNPJ inválido."));
    }

    @Test
    void validacaoRetornaMapaDeCampos() throws Exception {
        EmpresaRequestDTO dto = createValidEmpresaRequest();
        dto.setEmail("email-invalido");

        mockMvc.perform(post("/internal/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    void corpoAusenteRetornaErroPadronizado() throws Exception {
        mockMvc.perform(post("/internal/empresas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST_BODY"));
    }

    @Test
    void excecaoGenericaNaoExpoeDetalhesInternos() throws Exception {
        EmpresaRequestDTO dto = createValidEmpresaRequest();
        Mockito.when(empresaService.cadastrarEmpresa(any(EmpresaRequestDTO.class)))
                .thenThrow(new RuntimeException("detalhe interno sensível"));

        mockMvc.perform(post("/internal/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.message").value("Erro interno no servidor. Tente novamente mais tarde."));
    }

    private EmpresaRequestDTO createValidEmpresaRequest() {
        EmpresaRequestDTO dto = new EmpresaRequestDTO();
        dto.setDocumentNumber("17395568000151");
        dto.setRazaoSocial("Empresa Teste Ltda");
        dto.setNomeFantasia("Empresa Teste");
        dto.setTelefone("11999999999");
        dto.setEmail("empresa@teste.com");
        dto.setNomeTitular("Teste Nome");
        dto.setRua("Rua Teste");
        dto.setNumero("100");
        dto.setComplemento("Sala 1");
        dto.setBairro("Centro");
        dto.setCidade("Sao Paulo");
        dto.setEstado("SP");
        dto.setCep("01001000");
        return dto;
    }
}
