package com.mypetadmin.ps_empresa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypetadmin.ps_empresa.dto.EmpresaContratoStatusDTO;
import com.mypetadmin.ps_empresa.service.EmpresaService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = ContratoCallbackController.class)
class ContratoCallbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpresaService empresaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveSincronizarStatusDaEmpresaComContrato() throws Exception {
        UUID empresaId = UUID.randomUUID();

        EmpresaContratoStatusDTO dto = new EmpresaContratoStatusDTO();
        dto.setEmpresaId(empresaId);
        dto.setStatusContrato("ATIVO");

        doNothing().when(empresaService).sincronizarStatusComContrato(any(EmpresaContratoStatusDTO.class));

        mockMvc.perform(patch("/internal/contratos/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        ArgumentCaptor<EmpresaContratoStatusDTO> captor =
                ArgumentCaptor.forClass(EmpresaContratoStatusDTO.class);

        verify(empresaService, times(1)).sincronizarStatusComContrato(captor.capture());

        assertEquals(empresaId, captor.getValue().getEmpresaId());
        assertEquals("ATIVO", captor.getValue().getStatusContrato());
    }

    @Test
    void deveRetornar400QuandoJsonInvalido() throws Exception {
        mockMvc.perform(patch("/internal/contratos/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ json-invalido }"))
                .andExpect(status().isBadRequest());
    }
}