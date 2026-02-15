package com.mypetadmin.ps_empresa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypetadmin.ps_empresa.dto.EmpresaRequestDTO;
import com.mypetadmin.ps_empresa.dto.EmpresaResponseDTO;
import com.mypetadmin.ps_empresa.dto.PageResponse;
import com.mypetadmin.ps_empresa.dto.UpdateEmpresaRequestDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
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

        List<EmpresaResponseDTO> lista = Arrays.asList(empresa1, empresa2);


        PageResponse<EmpresaResponseDTO> empresas =
                new PageResponse<>(lista, 0, 10, lista.size(), 1);

        when(empresaService.getAllEmpresaSorted(
                any(), any(), any(), any(), anyInt(), anyInt(), any(), any()
        )).thenReturn(empresas);

        mockMvc.perform(get("/empresas/buscaEmpresas")
                        .param("status", StatusEmpresa.AGUARDANDO_PAGAMENTO.name())
                        .param("razaoSocial", "Pet Shop")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].razaoSocial").value("Pet Shop ABC"))
                .andExpect(jsonPath("$.content[1].razaoSocial").value("Pet Shop XYZ"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void buscarEmpresas_semResultados_retornaListaVazia() throws Exception {

        PageResponse<EmpresaResponseDTO> page =
                new PageResponse<>(Collections.emptyList(), 0, 10, 0, 0);

        when(empresaService.getAllEmpresaSorted(any(), any(), any(), any(), anyInt(), anyInt(), any(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/empresas/buscaEmpresas")
                        .param("documentNumber", "00000000000000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));


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

    @Test
    void editEmpresaById_quandoDadosValidos_entaoRetorna200() throws Exception {
        UUID empresaId = UUID.randomUUID();

        UpdateEmpresaRequestDto updateDto = new UpdateEmpresaRequestDto();
        updateDto.setNomeFantasia("PetShop Atualizado");
        updateDto.setTelefone("41999999999");
        updateDto.setEmail("novoemail@teste.com");
        updateDto.setRua("Rua Nova");
        updateDto.setNumero("123");
        updateDto.setComplemento("Sala 2");
        updateDto.setBairro("Centro");
        updateDto.setCidade("Curitiba");
        updateDto.setEstado("PR");
        updateDto.setCep("80000000");

        EmpresaResponseDTO responseDto = new EmpresaResponseDTO(
                empresaId,
                "12345678000199",
                "Razão Social LTDA",
                "PetShop Atualizado",
                "41999999999",
                "novoemail@teste.com",
                "João",
                "80000000",
                "Curitiba",
                "PR",
                "Rua Nova, 123 - Sala 2, Centro",
                StatusEmpresa.ATIVO
        );

        when(empresaService.editEmpresaById(eq(empresaId), any(UpdateEmpresaRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(put("/empresas/editEmpresa/{id}", empresaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(empresaId.toString()))
                .andExpect(jsonPath("$.nomeFantasia").value("PetShop Atualizado"))
                .andExpect(jsonPath("$.telefone").value("41999999999"))
                .andExpect(jsonPath("$.email").value("novoemail@teste.com"))
                .andExpect(jsonPath("$.endereco").value("Rua Nova, 123 - Sala 2, Centro"));
    }

    @Test
    void editEmpresaById_quandoEmpresaNaoEncontrada_entaoRetorna404() throws Exception {
        UUID empresaId = UUID.randomUUID();

        UpdateEmpresaRequestDto updateDto = new UpdateEmpresaRequestDto();
        updateDto.setNomeFantasia("Nome qualquer");

        when(empresaService.editEmpresaById(eq(empresaId), any(UpdateEmpresaRequestDto.class)))
                .thenThrow(new EmpresaNaoEncontradaException("Empresa não encontrada"));

        mockMvc.perform(put("/empresas/editEmpresa/{id}", empresaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Empresa não encontrada"));
    }

    @Test
    void editEmpresaById_quandoRequestBodyNulo_entaoAceitaERetorna200() throws Exception {
        UUID empresaId = UUID.randomUUID();

        EmpresaResponseDTO responseDto = new EmpresaResponseDTO();
        responseDto.setId(empresaId);
        responseDto.setNomeFantasia("Empresa Existente");
        responseDto.setStatus(StatusEmpresa.ATIVO);

        when(empresaService.editEmpresaById(eq(empresaId), any(UpdateEmpresaRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(put("/empresas/editEmpresa/{id}", empresaId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(empresaId.toString()))
                .andExpect(jsonPath("$.nomeFantasia").value("Empresa Existente"));
    }



}
