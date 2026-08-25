package com.mypetadmin.ps_empresa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypetadmin.ps_empresa.dto.EmpresaResponseDTO;
import com.mypetadmin.ps_empresa.dto.PageResponse;
import com.mypetadmin.ps_empresa.dto.UpdateEmpresaRequestDto;
import com.mypetadmin.ps_empresa.enums.StatusEmpresa;
import com.mypetadmin.ps_empresa.exception.EmpresaNaoEncontradaException;
import com.mypetadmin.ps_empresa.service.EmpresaService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(EmpresaController.class)
class EmpresaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpresaService empresaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void buscarEmpresasComFiltrosRetornaPagina() throws Exception {
        EmpresaResponseDTO empresa = new EmpresaResponseDTO();
        empresa.setId(UUID.randomUUID());
        empresa.setRazaoSocial("Pet Shop ABC");
        empresa.setDocumentNumber("12345678000199");
        empresa.setStatus(StatusEmpresa.ATIVO);

        PageResponse<EmpresaResponseDTO> page = new PageResponse<>(List.of(empresa), 0, 10, 1, 1);
        when(empresaService.getAllEmpresaSorted(any(), any(), any(), any(), anyInt(), anyInt(), any(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/empresas")
                        .param("status", StatusEmpresa.ATIVO.name())
                        .param("razaoSocial", "Pet Shop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].razaoSocial").value("Pet Shop ABC"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getEmpresaByIdRetornaEmpresa() throws Exception {
        UUID empresaId = UUID.randomUUID();
        EmpresaResponseDTO response = new EmpresaResponseDTO();
        response.setId(empresaId);
        response.setRazaoSocial("Pet Shop Teste");

        when(empresaService.getEmpresaById(empresaId)).thenReturn(response);

        mockMvc.perform(get("/empresas/{id}", empresaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(empresaId.toString()))
                .andExpect(jsonPath("$.razaoSocial").value("Pet Shop Teste"));
    }

    @Test
    void getEmpresaByIdNaoEncontradaRetorna404Padronizado() throws Exception {
        UUID empresaId = UUID.randomUUID();
        when(empresaService.getEmpresaById(empresaId))
                .thenThrow(new EmpresaNaoEncontradaException("Empresa não encontrada"));

        mockMvc.perform(get("/empresas/{id}", empresaId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("EMPRESA_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Empresa não encontrada"));
    }

    @Test
    void deleteEmpresaByIdRetorna204() throws Exception {
        UUID empresaId = UUID.randomUUID();
        Mockito.doNothing().when(empresaService).deleteEmpresaById(empresaId);

        mockMvc.perform(delete("/empresas/{id}", empresaId))
                .andExpect(status().isNoContent());
    }

    @Test
    void editEmpresaByIdComDadosValidosRetorna200() throws Exception {
        UUID empresaId = UUID.randomUUID();
        UpdateEmpresaRequestDto update = new UpdateEmpresaRequestDto();
        update.setNomeFantasia("PetShop Atualizado");
        update.setTelefone("41999999999");
        update.setEmail("novoemail@teste.com");

        EmpresaResponseDTO response = new EmpresaResponseDTO();
        response.setId(empresaId);
        response.setNomeFantasia("PetShop Atualizado");
        response.setTelefone("41999999999");
        response.setEmail("novoemail@teste.com");

        when(empresaService.editEmpresaById(eq(empresaId), any(UpdateEmpresaRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/empresas/{id}", empresaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeFantasia").value("PetShop Atualizado"))
                .andExpect(jsonPath("$.email").value("novoemail@teste.com"));
    }

    @Test
    void editEmpresaByIdAplicaValidacaoDoDto() throws Exception {
        UUID empresaId = UUID.randomUUID();
        UpdateEmpresaRequestDto update = new UpdateEmpresaRequestDto();
        update.setTelefone("123");

        mockMvc.perform(patch("/empresas/{id}", empresaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors.telefone").exists());
    }

    @Test
    void editEmpresaByIdSemBodyRetorna400() throws Exception {
        mockMvc.perform(patch("/empresas/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST_BODY"));
    }
}
