package com.mypetadmin.ps_empresa.controller;

import com.mypetadmin.ps_empresa.dto.EmpresaRequestDTO;
import com.mypetadmin.ps_empresa.dto.EmpresaResponseDTO;
import com.mypetadmin.ps_empresa.service.EmpresaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            return ResponseEntity.ok(empresaCadastrada);
        } catch (Exception e) {
            log.error("Error ao cadastrar empresa: {}", e.getMessage(), e);
            throw e;
        }

    }
}
