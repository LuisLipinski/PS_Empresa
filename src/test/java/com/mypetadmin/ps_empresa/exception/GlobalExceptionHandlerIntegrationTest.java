package com.mypetadmin.ps_empresa.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypetadmin.ps_empresa.controller.EmpresaController;
import com.mypetadmin.ps_empresa.dto.EmpresaRequestDTO;
import com.mypetadmin.ps_empresa.dto.EmpresaResponseDTO;
import com.mypetadmin.ps_empresa.enums.StatusEmpresa;
import com.mypetadmin.ps_empresa.exception.GlobalExceptionHandler;
import com.mypetadmin.ps_empresa.exception.*;
import com.mypetadmin.ps_empresa.service.EmpresaService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EmpresaController.class)
public class GlobalExceptionHandlerIntegrationTest {

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
        dto.setEmail("empresa@teste.com");
        dto.setNomeTitular("Teste Nome");
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
    public void testCadastroEmpresa_Sucesso() throws Exception {
        EmpresaRequestDTO dto = createValidEmpresaRequest();
        EmpresaResponseDTO responseDTO = new EmpresaResponseDTO();
        responseDTO.setId(UUID.randomUUID());
        responseDTO.setDocumentNumber(dto.getDocumentNumber());
        responseDTO.setStatus(StatusEmpresa.AGUARDANDO_PAGAMENTO);
        responseDTO.setRazaoSocial(dto.getRazaoSocial());
        responseDTO.setNomeFantasia(dto.getNomeFantasia());
        responseDTO.setEmail(dto.getEmail());
        responseDTO.setTelefone(dto.getTelefone());
        responseDTO.setCep(dto.getCep());
        responseDTO.setCidade(dto.getCidade());
        responseDTO.setEstado(dto.getEstado());
        responseDTO.setEndereco(dto.getRua() + ", " + dto.getNumero() + (dto.getComplemento() != null ? " " + dto.getComplemento() : "") + ", " + dto.getBairro());

        Mockito.when(empresaService.cadastrarEmpresa(any(EmpresaRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/empresas/createEmpresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(responseDTO.getId().toString()))
                .andExpect(jsonPath("$.documentNumber").value(dto.getDocumentNumber()))
                .andExpect(jsonPath("$.status").value("AGUARDANDO_PAGAMENTO"));
    }

    @Test
    public void testCadastroEmpresa_EmpresaExistenteException() throws Exception {
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
    public void testCadastroEmpresa_EmailExistenteException() throws Exception {
        EmpresaRequestDTO dto = createValidEmpresaRequest();

        Mockito.when(empresaService.cadastrarEmpresa(any(EmpresaRequestDTO.class)))
                .thenThrow(new EmailExistenteException("Email já cadastrado no sistema, informe outro email."));

        mockMvc.perform(post("/empresas/createEmpresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email já cadastrado no sistema, informe outro email.")));
    }

    @Test
    public void testCadastroEmpresa_IllegalArgumentException() throws Exception {
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
    public void testCadastroEmpresa_GenericException() throws Exception {
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
    public void testCadastroEmpresa_CnpjInvalidException() throws Exception {
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
    public void testCadastroEmpresa_MethodArgumentNotValidException() throws Exception {
        EmpresaRequestDTO dto = createValidEmpresaRequest();
        dto.setEmail("email-invalido");

        mockMvc.perform(post("/empresas/createEmpresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Email inválido: deve ter ao menos 3 caracteres antes do @ e um domínio válido (ex: .com, .org)"));
    }

    @Test
    public void testCadastroEmpresa_MissingServletRequestParameterException() throws Exception {
        mockMvc.perform(post("/empresas/createEmpresas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Corpo da requisição ausente ou inválido."));
    }
}
