package com.mypetadmin.ps_empresa.controller;

import com.mypetadmin.ps_empresa.dto.EmpresaRequestDTO;
import com.mypetadmin.ps_empresa.dto.EmpresaResponseDTO;
import com.mypetadmin.ps_empresa.enums.StatusEmpresa;
import com.mypetadmin.ps_empresa.service.EmpresaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(InternalEmpresaController.class)
class InternalEmpresaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmpresaService empresaService;

    @Test
    void deveCadastrarEmpresaParaOrquestrador() throws Exception {
        EmpresaRequestDTO request = requestValido();
        UUID empresaId = UUID.randomUUID();

        EmpresaResponseDTO response = new EmpresaResponseDTO();
        response.setId(empresaId);
        response.setDocumentNumber(request.getDocumentNumber());
        response.setStatus(StatusEmpresa.AGUARDANDO_CONTRATO);

        when(empresaService.cadastrarEmpresa(any(EmpresaRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/internal/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/internal/empresas/" + empresaId))
                .andExpect(jsonPath("$.id").value(empresaId.toString()))
                .andExpect(jsonPath("$.status").value("AGUARDANDO_CONTRATO"));
    }

    @Test
    void deveConsultarSomenteStatusDaEmpresa() throws Exception {
        UUID empresaId = UUID.randomUUID();
        EmpresaResponseDTO response = new EmpresaResponseDTO();
        response.setId(empresaId);
        response.setStatus(StatusEmpresa.ATIVO);

        when(empresaService.getEmpresaById(empresaId)).thenReturn(response);

        mockMvc.perform(get("/internal/empresas/{id}/status", empresaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empresaId").value(empresaId.toString()))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    void deveValidarPayloadDeCadastro() throws Exception {
        EmpresaRequestDTO request = new EmpresaRequestDTO();

        mockMvc.perform(post("/internal/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    private EmpresaRequestDTO requestValido() {
        EmpresaRequestDTO request = new EmpresaRequestDTO();
        request.setDocumentNumber("17395568000151");
        request.setRazaoSocial("Empresa Teste");
        request.setNomeFantasia("Fantasia Teste");
        request.setTelefone("41999999999");
        request.setEmail("teste@empresa.com");
        request.setNomeTitular("Teste Titular");
        request.setRua("Rua A");
        request.setNumero("123");
        request.setComplemento("Bloco A");
        request.setBairro("Bairro Central");
        request.setCidade("Curitiba");
        request.setEstado("PR");
        request.setCep("01001000");
        return request;
    }
}
