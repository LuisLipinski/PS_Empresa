package com.mypetadmin.ps_empresa.controller;

import com.mypetadmin.ps_empresa.dto.AtualizacaoStatusRequestDTO;
import com.mypetadmin.ps_empresa.dto.AtualizacaoStatusResponseDTO;
import com.mypetadmin.ps_empresa.dto.EmpresaRequestDTO;
import com.mypetadmin.ps_empresa.dto.EmpresaResponseDTO;
import com.mypetadmin.ps_empresa.enums.StatusEmpresa;
import com.mypetadmin.ps_empresa.service.EmpresaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/empresas")
@RequiredArgsConstructor
@Slf4j
public class EmpresaController {

    private final EmpresaService empresaService;

    @Operation(summary = "Cadastra nova empresa sem a necessidade de token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Empresa criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição invalida")
    })
    @PostMapping("/createEmpresas")
    public ResponseEntity<EmpresaResponseDTO>cadastrarEmpresa(@Valid @RequestBody EmpresaRequestDTO empresaRequestDTO) {
        log.info("Recebendo requisição para cadastrar empresa: CNPJ={}, Razão Social={}, Nome Fantasia={}, Telefone={}, Email={}, " +
                        "Endereco={}, {}, {}, {}, Cidade={}, Estado={}, CEP={}",
                empresaRequestDTO.getDocumentNumber(), empresaRequestDTO.getRazaoSocial(), empresaRequestDTO.getNomeFantasia(),
                empresaRequestDTO.getTelefone(), empresaRequestDTO.getEmail(), empresaRequestDTO.getRua(), empresaRequestDTO.getNumero(),
                empresaRequestDTO.getComplemento(), empresaRequestDTO.getBairro(), empresaRequestDTO.getCidade(), empresaRequestDTO.getEstado(),
                empresaRequestDTO.getCep());
        try {
            EmpresaResponseDTO empresaCadastrada = empresaService.cadastrarEmpresa(empresaRequestDTO);
            log.info("Empresa cadastrada com sucesso: id={}, CNPJ={}, Status={}",
                    empresaCadastrada.getId(), empresaCadastrada.getDocumentNumber(), empresaCadastrada.getStatus());
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id")
                    .buildAndExpand(empresaCadastrada.getId())
                    .toUri();
            return ResponseEntity.created(location).body(empresaCadastrada);
        } catch (Exception e) {
            log.error("Error ao cadastrar empresa: {}", e.getMessage(), e);
            throw e;
        }

    }

    @Operation(summary = "Atualiza o status de uma empresa sem a necessidade de token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição invalida")
    })
    @PutMapping("/atualizarStatus")
    public ResponseEntity<AtualizacaoStatusResponseDTO> atualizarStatus(@RequestParam @Valid UUID empresaId,
                                                                        @RequestParam @Valid StatusEmpresa novoStatus) {
        log.info("Recebendo requisição para atualização do status da empresa com id ${} para o status ${}.", empresaId, novoStatus);

        try {
            empresaService.atualizarStatus(empresaId, novoStatus);
            log.info("Status da empresa com o id ${} foi alterado para ${} com sucesso.", empresaId, novoStatus);
            AtualizacaoStatusResponseDTO response = AtualizacaoStatusResponseDTO.builder()
                    .message("Status atualizado com sucesso")
                    .statusCode(HttpStatus.OK.value())
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error ao atualizar o status: {}", e.getMessage(), e);
            throw e;
        }
    }
}
