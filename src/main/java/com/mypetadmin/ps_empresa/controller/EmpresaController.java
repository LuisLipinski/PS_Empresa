package com.mypetadmin.ps_empresa.controller;

import com.mypetadmin.ps_empresa.dto.EmpresaResponseDTO;
import com.mypetadmin.ps_empresa.dto.PageResponse;
import com.mypetadmin.ps_empresa.dto.UpdateEmpresaRequestDto;
import com.mypetadmin.ps_empresa.enums.DirectionField;
import com.mypetadmin.ps_empresa.enums.SortField;
import com.mypetadmin.ps_empresa.enums.StatusEmpresa;
import com.mypetadmin.ps_empresa.service.EmpresaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/empresas")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaService empresaService;

    @Operation(summary = "Busca empresas com filtros, paginação e ordenação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @GetMapping
    public ResponseEntity<PageResponse<EmpresaResponseDTO>> buscarEmpresas(
            @RequestParam(required = false) String documentNumber,
            @RequestParam(required = false) String razaoSocial,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) StatusEmpresa status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DOCUMENT_NUMBER") SortField sortField,
            @RequestParam(defaultValue = "ASC") DirectionField directionField) {

        return ResponseEntity.ok(
                empresaService.getAllEmpresaSorted(
                        documentNumber,
                        razaoSocial,
                        email,
                        status,
                        page,
                        size,
                        sortField,
                        directionField
                )
        );
    }

    @Operation(summary = "Busca uma empresa pelo id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Empresa encontrada"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> getEmpresaById(@PathVariable UUID id) {
        return ResponseEntity.ok(empresaService.getEmpresaById(id));
    }

    @Operation(summary = "Atualiza parcialmente os dados de uma empresa")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Empresa atualizada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada"),
            @ApiResponse(responseCode = "409", description = "Conflito de dados")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> editEmpresaById(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEmpresaRequestDto updateEmpresa) {
        return ResponseEntity.ok(empresaService.editEmpresaById(id, updateEmpresa));
    }

    @Operation(summary = "Exclui uma empresa pelo id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Empresa excluída"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmpresaById(@PathVariable UUID id) {
        empresaService.deleteEmpresaById(id);
        return ResponseEntity.noContent().build();
    }
}
